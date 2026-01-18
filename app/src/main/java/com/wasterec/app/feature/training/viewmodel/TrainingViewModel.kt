package com.wasterec.app.feature.training.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.navigation.NavHostController
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.wasterec.app.feature.importimage.viewmodel.ImportImageViewModel
import com.wasterec.app.manager.ClassifierWeightFileManager
import com.wasterec.app.manager.EfficientNetB0
import com.wasterec.app.model.ClassifierWeightModel
import com.wasterec.app.model.ModelConiguration
import com.wasterec.app.model.globalmodel.GlobalWeightModel
import com.wasterec.app.repositories.GlobalModelRepository
import com.wasterec.app.utils.encodeWeightsToBase64
import com.wasterec.app.utils.floatArrayToBase64
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class TrainingViewModel(
    application : Application,
    val context : Context,
    val navHostController: NavHostController,
    val importImageViewModel: ImportImageViewModel
) : AndroidViewModel(application) {
    var efficientNetB0 : EfficientNetB0 = EfficientNetB0(context)
    val modelProducer : MutableState<CartesianChartModelProducer> = mutableStateOf(
        CartesianChartModelProducer())
    var currentEpoch : MutableState<Int>  = mutableIntStateOf(0)
    var currentLoss : MutableState<Float> = mutableFloatStateOf(0f)
    val lossList = mutableStateListOf<Float>()
    val classifierWeightFileManager : ClassifierWeightFileManager = ClassifierWeightFileManager(context)
    var modelConfig = ModelConiguration(
        learningRate = 0.001f,
        epoch = 5,
        batchSize = 2
    )

    //Fungsi untuk reinit nilai atau reset variabel
    @RequiresApi(Build.VERSION_CODES.O)
    fun reInit(){
        val backboneModelFile = File(context.filesDir, "Backbone.ptl")
        if(backboneModelFile.exists()){
            efficientNetB0 = EfficientNetB0(context, "Backbone.ptl")
            println("Berhasil mengupdate backbone terbaru ")
        }
        CoroutineScope(Dispatchers.IO).launch {
            val newClassifierParam : ClassifierWeightModel? = classifierWeightFileManager.loadClassifierParamFromFile()
            newClassifierParam?.let{ newParam ->
                efficientNetB0.setClassifierWeightAndBias(newParam.weights, newParam.bias)
                println("Berhasil memuat classifier param tebaru")
            }
            startLocalTraining()
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    //Fungsi buat memanggil model dan mulai training local
    fun startLocalTraining(){
        CoroutineScope(Dispatchers.IO).launch {
            val data = efficientNetB0.train(
                config = modelConfig,
                dataset = importImageViewModel.imageDatasetList,
                onProgressUpdate = { epoch, loss ->
                    println("Progress pelatihan ${epoch}")
                    currentEpoch.value = epoch+1
                    currentLoss.value = loss
                    if (!loss.isNaN() && !loss.isInfinite()) {
                        lossList.add(loss)
                        CoroutineScope(Dispatchers.IO).launch {
                            modelProducer.value.runTransaction {
                                lineSeries {
                                    // Vico menerima List untuk X dan List untuk Y
                                    series(
                                        x = lossList.indices.toList(), // x = 0, 1, 2, ...
                                        y = lossList                   // y = nilai loss
                                    )
                                }
                            }
                        }
                    }

                    // 3. Update Vico Chart
                    // Gunakan tryRunTransaction agar thread-safe dan efisien



                }
            )

            println(data.get("weights"))

            GlobalModelRepository().uploadModelWeight(globalWeightModel = GlobalWeightModel(
                num_sample = importImageViewModel.imageDatasetList.size,
                label_count = importImageViewModel.getEachLabelCount(),
                weights = encodeWeightsToBase64(data.get("weights") as Array<FloatArray>),
                bias = floatArrayToBase64(data.getValue("bias") as FloatArray),
            )
            )
        }
    }


}