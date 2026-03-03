package com.wasterec.app.feature.anotate.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.navigation.NavHostController
import com.wasterec.app.manager.DatasetManager
import com.wasterec.app.manager.EfficientNetB0
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class AnnotateViewModel(application : Application, val context: Context, val navHostController: NavHostController, val datasetManager: DatasetManager): AndroidViewModel(application = application) {
    var currentAnnotateIndex : MutableState<Int> = mutableIntStateOf(0)
    var showLabelSelectionMenu : MutableState<Boolean> = mutableStateOf(false)
    var predictResult : MutableState<String> = mutableStateOf("")
    var confidentLevel : MutableState<Float> = mutableFloatStateOf(0f)
    var predictedLabel : MutableState<Int> = mutableIntStateOf(0)
    var currentBitmap : MutableState<Bitmap?> = mutableStateOf(null)
    var efficientNetB0 : EfficientNetB0? = EfficientNetB0(context, "Backbone.ptl" )
    val label = arrayOf("Plastik", "Kertas", "Kaca",  "Logam", "Kardus", "Sampah")
    var isModelLoading : MutableState<Boolean> = mutableStateOf(true)

    //Deklarasikan ulang semua nilai variabel
    @RequiresApi(Build.VERSION_CODES.O)
    fun reInit(){
        isModelLoading.value = true
        currentBitmap.value = null
        currentAnnotateIndex.value = 0
        showLabelSelectionMenu.value = false
        predictResult.value = ""
        confidentLevel.value = 0f
        predictedLabel.value = 0
        currentBitmap.value = null
        val globalModelFile = File(context.filesDir, "Backbone.ptl")
        if(globalModelFile.exists()){
            println("Ada file ${globalModelFile.absolutePath}")
            efficientNetB0 = EfficientNetB0(context, "Backbone.ptl")
            this.currentBitmap.value?.let { bmp ->
                this.classifyImage(bmp)
            }
        }
        isModelLoading.value = false
    }



    @RequiresApi(Build.VERSION_CODES.O)
    fun classifyImage(bmp : Bitmap ){
        currentBitmap.value = bmp
        efficientNetB0?.let { efficientNetB0 ->
            CoroutineScope(Dispatchers.IO).launch{
                predictResult.value = "Loading..."
                withContext(Dispatchers.IO) {
                    val output = efficientNetB0.backbonePredict(
                        bmp.copy(Bitmap.Config.ARGB_8888, false)
                    )
                    val outputIdx = output.first
                    val labelResult = label.getOrNull(outputIdx)
                    withContext(Dispatchers.Main) {
                        predictResult.value = labelResult ?: "Unknown"
                        println("Label : " + labelResult)
                        println("Index Label : " + outputIdx)
                        confidentLevel.value = output.second
                        if (confidentLevel.value == 0.0f) {
                            predictResult.value = "Unknown"
                        }
                        predictedLabel.value = outputIdx
                    }
                }
            }
        }
    }
}