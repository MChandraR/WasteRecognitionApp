package com.wasterec.app.feature.neural_search

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wasterec.app.manager.EfficientNetB0
import com.wasterec.app.model.ModelConfiguration
import com.wasterec.app.model.TrainingModel
import com.wasterec.app.utils.DirichletSampler
import com.wasterec.app.utils.format
import com.wasterec.app.utils.resizeAndCropCenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.pytorch.IValue
import org.pytorch.torchvision.TensorImageUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.min

data class ImageSample(val bitmap: Bitmap, val labelIndex: Int)
class NSViewModel(val app: Application) : AndroidViewModel(app) {
    val efficientNetB0  : EfficientNetB0?= null
    val minVal  = mutableStateOf(100.0)
    val maxVal = mutableStateOf(0.0)
    val labelPath = Array<File>(6) { File("") }
    val stringText = mutableStateOf("")

    // List untuk menyimpan semua gambar dan labelnya
    val datasetImages = mutableListOf<TrainingModel>()
    val modelConfigurations = arrayOf(
        ModelConfiguration(0.1f, 10, 8), ModelConfiguration(0.1f, 20, 8), ModelConfiguration(0.1f, 30, 8), ModelConfiguration(0.1f, 40, 8), ModelConfiguration(0.1f, 50, 8),
        // Batch 16
        ModelConfiguration(0.1f, 10, 16), ModelConfiguration(0.1f, 20, 16), ModelConfiguration(0.1f, 30, 16), ModelConfiguration(0.1f, 40, 16), ModelConfiguration(0.1f, 50, 16),
        // Batch 32
        ModelConfiguration(0.1f, 10, 32), ModelConfiguration(0.1f, 20, 32), ModelConfiguration(0.1f, 30, 32), ModelConfiguration(0.1f, 40, 32), ModelConfiguration(0.1f, 50, 32),

        // === LR 0.01 (Standard) ===
        // Batch 8
        ModelConfiguration(0.01f, 10, 8), ModelConfiguration(0.01f, 20, 8), ModelConfiguration(0.01f, 30, 8), ModelConfiguration(0.01f, 40, 8), ModelConfiguration(0.01f, 50, 8),
        // Batch 16
        ModelConfiguration(0.01f, 10, 16), ModelConfiguration(0.01f, 20, 16), ModelConfiguration(0.01f, 30, 16), ModelConfiguration(0.01f, 40, 16), ModelConfiguration(0.01f, 50, 16),
        // Batch 32
        ModelConfiguration(0.01f, 10, 32), ModelConfiguration(0.01f, 20, 32), ModelConfiguration(0.01f, 30, 32), ModelConfiguration(0.01f, 40, 32), ModelConfiguration(0.01f, 50, 32),

        // === LR 0.001 (Stable) ===
        // Batch 8
        ModelConfiguration(0.001f, 10, 8), ModelConfiguration(0.001f, 20, 8), ModelConfiguration(0.001f, 30, 8), ModelConfiguration(0.001f, 40, 8), ModelConfiguration(0.001f, 50, 8),
        // Batch 16
        ModelConfiguration(0.001f, 10, 16), ModelConfiguration(0.001f, 20, 16), ModelConfiguration(0.001f, 30, 16), ModelConfiguration(0.001f, 40, 16), ModelConfiguration(0.001f, 50, 16),
        // Batch 32
        ModelConfiguration(0.001f, 10, 32), ModelConfiguration(0.001f, 20, 32), ModelConfiguration(0.001f, 30, 32), ModelConfiguration(0.001f, 40, 32), ModelConfiguration(0.001f, 50, 32),

        // === LR 0.0001 (Fine-Tuning) ===
        // Batch 8
        ModelConfiguration(0.0001f, 10, 8), ModelConfiguration(0.0001f, 20, 8), ModelConfiguration(0.0001f, 30, 8), ModelConfiguration(0.0001f, 40, 8), ModelConfiguration(0.0001f, 50, 8),
        // Batch 16
        ModelConfiguration(0.0001f, 10, 16), ModelConfiguration(0.0001f, 20, 16), ModelConfiguration(0.0001f, 30, 16), ModelConfiguration(0.0001f, 40, 16), ModelConfiguration(0.0001f, 50, 16),
        // Batch 32
        ModelConfiguration(0.0001f, 10, 32), ModelConfiguration(0.0001f, 20, 32), ModelConfiguration(0.0001f, 30, 32), ModelConfiguration(0.0001f, 40, 32), ModelConfiguration(0.0001f, 50, 32)
    )

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadDataset() {
        viewModelScope.launch {
            try {
                val assetManager = app.baseContext.assets
                val folders = assetManager.list("dataset")

                if (folders != null && folders.isNotEmpty()) {
                    datasetImages.clear() // Bersihkan jika ingin load ulang

                    for ((idx, labelName) in folders.withIndex()) {
                        if (idx >= labelPath.size) break // Hindari IndexOutOfBounds

                        // 1. Salin asset ke storage fisik
                        val folderPath =
                            getAbsolutePathFromAsset(app.baseContext, "dataset/$labelName")
                        val folderFile = File(folderPath)
                        labelPath[idx] = folderFile

                        // 2. Ambil semua file gambar di dalam folder tersebut
                        val images = folderFile.listFiles()
                        images?.forEach { imageFile ->
                            if (imageFile.isFile && isImageFile(imageFile.name)) {
                                // 3. Decode file menjadi Bitmap
                                val bitmap = resizeAndCropCenter( BitmapFactory.decodeFile(imageFile.absolutePath))
                                if (bitmap != null) {
                                    datasetImages.add(TrainingModel(bitmap, idx, arrayOf()))
                                    //println("Loaded: ${imageFile.name} as Label $idx")
                                }
                            }
                        }
                    }
                    println("Total gambar berhasil dimuat: ${datasetImages.size}")
                }

                featureExtraction()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun featureExtraction(){
        println("Memulai training")
        val featureList = if(efficientNetB0 == null) null else datasetImages.map { data ->
            val safeBitmap = if (data.Input.config == Bitmap.Config.HARDWARE) {
                data.Input.copy(Bitmap.Config.ARGB_8888, false)
            } else { data.Input }

            val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
                safeBitmap,
                TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
                TensorImageUtils.TORCHVISION_NORM_STD_RGB
            )
            val feat = efficientNetB0.model.forward(IValue.from(inputTensor))
                .toTensor().dataAsFloatArray

                Pair(feat, data.Label)

        }
        val listOfLoss = mutableListOf<List<Float>>()

        modelConfigurations.forEachIndexed{ idx, config ->
            efficientNetB0?.loadClassifierParams {
                efficientNetB0.setClassifierWeight(it.first)
                efficientNetB0.setClassifierBias(it.second)
            }
            var losses = mutableListOf<Float>();
            featureList?.let{ featureList ->
                efficientNetB0?.trainWithFeature( featureList,config,datasetImages,
                    onProgressUpdate = { epoch, loss ->
                        losses.add(loss)
                    },
                    onFinished = {
                        listOfLoss.add(idx, losses)
                    }
                )
            }

        }
        var lossStr = "";
        for(loss in listOfLoss){
            lossStr += loss
            lossStr += ","
            println(loss)
        }
        stringText.value = lossStr
    }

    // Helper untuk validasi file gambar
    private fun isImageFile(fileName: String): Boolean {
        val extensions = listOf("jpg", "jpeg", "png", "bmp")
        return extensions.any { fileName.lowercase().endsWith(it) }
    }

    fun getAbsolutePathFromAsset(context: Context, assetPath: String): String {
        val destinationFile = File(context.cacheDir, assetPath)
        if (!destinationFile.parentFile.exists()) destinationFile.parentFile.mkdirs()

        val children = context.assets.list(assetPath)
        if (children != null && children.isNotEmpty()) {
            if (!destinationFile.exists()) destinationFile.mkdirs()
            for (child in children) {
                getAbsolutePathFromAsset(context, "$assetPath/$child")
            }
        } else {
            // Optimasi: Jangan salin ulang jika file sudah ada
            if (!destinationFile.exists()) {
                context.assets.open(assetPath).use { input ->
                    FileOutputStream(destinationFile).use { output ->
                        input.copyTo(output)
                    }
                }
            }
        }
        return destinationFile.absolutePath
    }

    fun sampler(){
        try {
            // Menjalankan perintah 'top' satu kali (batch mode)
            val process = Runtime.getRuntime().exec("top -n 1 -d 1")
            val reader = process.inputStream.bufferedReader()
            val output = reader.readText()

            // Cari baris yang mengandung package name aplikasi kamu
            val appLine = output.lines().find { it.contains(app.baseContext.packageName) }

            // Biasanya persentase CPU ada di kolom ke-9 atau ke-10 (tergantung versi Android)
            println(appLine ?: "Data tidak ditemukan")
        } catch (e: Exception) {
            "Error: ${e.message}"
        }

        var minVal = 1000.0
        viewModelScope.launch {
            val k = 6 // Misal membagi data ke 5 klien
            val alpha = 200.0 // Parameter konsentrasi
            for (i in 0 until 1000) {
                val sampler = DirichletSampler(alpha)
                val proportions = sampler.sample(k)

                println("Proporsi Pembagian Data (Alpha=$alpha):")
                proportions.forEachIndexed { index, prop ->
                    val percent = (prop * 100).format(2)
                    println("Klien $index: $percent%")
                    if( prop < minVal){

                        minVal = prop

                    }
                }
                //println("Total: ${proportions.sum()}")
                println("Min : ${minVal   }")
            }
        }





    }
}