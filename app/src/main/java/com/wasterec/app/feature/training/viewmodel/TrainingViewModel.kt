package com.wasterec.app.feature.training.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.navigation.NavHostController
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.wasterec.app.feature.anotate.viewmodel.AnnotateViewModel
import com.wasterec.app.feature.importimage.viewmodel.ImportImageViewModel
import com.wasterec.app.manager.ClassifierWeightFileManager
import com.wasterec.app.manager.DatasetManager
import com.wasterec.app.manager.EfficientNetB0
import com.wasterec.app.manager.FileManager
import com.wasterec.app.model.ClassifierWeightModel
import com.wasterec.app.model.Destination
import com.wasterec.app.model.ModelConiguration
import com.wasterec.app.model.globalmodel.GlobalWeightModel
import com.wasterec.app.repositories.DatasetUploadRepository
import com.wasterec.app.repositories.GlobalModelRepository
import com.wasterec.app.ui.color.ColorAsset
import com.wasterec.app.utils.encodeWeightsToBase64
import com.wasterec.app.utils.floatArrayToBase64
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class TrainingViewModel(
    val app : Application,
    val navHostController: NavHostController,
    val annotateViewModel: AnnotateViewModel,
    val datasetManager: MutableState<DatasetManager>
) : AndroidViewModel(app) {
    var efficientNetB0 : EfficientNetB0? = null
    val modelProducer : MutableState<CartesianChartModelProducer> = mutableStateOf(
        CartesianChartModelProducer())
    var currentEpoch : MutableState<Int>  = mutableIntStateOf(0)
    var currentLoss : MutableState<Float> = mutableFloatStateOf(0f)
    val lossList = mutableStateListOf<Float>()
    val classifierWeightFileManager : ClassifierWeightFileManager = ClassifierWeightFileManager(app.baseContext)
    val totalDatasetCount: MutableIntState = mutableIntStateOf(0)
    val datasetRepository : DatasetUploadRepository = DatasetUploadRepository(app.baseContext)
    val fileManager : FileManager = FileManager(app.baseContext)
    val totalLabelCount : MutableList<Int> = mutableListOf(0,0,0,0,0,0)
    val label = arrayOf("Plastik", "Kertas", "Kaca",  "Logam", "Kardus", "Sampah")
    val modelAccuracy = mutableIntStateOf(0)
    val globalModelRepository = GlobalModelRepository(app.baseContext)

    var modelConfig = ModelConiguration(
        learningRate = 0.001f,
        epoch = 300
    )


    fun getModelAccuracy():Int{
        val accuracy = (annotateViewModel?.rightLabelCount?.intValue?:0).toFloat() / (annotateViewModel?.datasetManager?.getDataSize()?:1).toFloat()
        return (accuracy * 100).toInt()
    }

    //Fungsi untuk reinit nilai atau reset variabel
    @RequiresApi(Build.VERSION_CODES.O)
    fun reInit(){
        modelAccuracy.intValue = getModelAccuracy()
        totalDatasetCount.intValue = datasetManager.value.getDataSize()
        datasetManager.value.getEachLabelCount().forEachIndexed { idx, value ->
            totalLabelCount[idx] = value
        }

        val backboneModelFile = File(app.baseContext.filesDir, "Backbone.ptl")
        if(backboneModelFile.exists()){
            efficientNetB0 = EfficientNetB0(app.baseContext, "Backbone.ptl")
            println("Berhasil mengupdate backbone terbaru ")
        }

        CoroutineScope(Dispatchers.IO).launch {
            val newClassifierParam : ClassifierWeightModel? = classifierWeightFileManager.loadClassifierParamFromFile()
            newClassifierParam?.let{ newParam ->
                efficientNetB0?.setClassifierWeightAndBias(newParam.weights, newParam.bias)
                println("Berhasil memuat classifier param tebaru")
            }
            startLocalTraining()
            modelProducer.value.runTransaction {

            }
        }

        //UPload dataset ke server
        CoroutineScope(Dispatchers.IO).launch{
            val dataset : List<Bitmap> = annotateViewModel.datasetManager.getData().map { it.Input }
            val datasetToUpload = fileManager.convertBitmapToZipFile(dataset, File(app.baseContext.cacheDir, "Dataset.zip") )
            datasetToUpload?.let {
                datasetRepository.uploadDatasetToServer(it)
                println("Berhasil upload dataset ke server")
            }
        }

        lossList.clear()

    }

    fun updateLossChartData(){
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

    fun clearTrainingData(){
        annotateViewModel.datasetManager.clearAlLData()
        annotateViewModel.trainingData.clear()
    }

    fun navigateToFinishTrainingView(){
        clearTrainingData()
        navHostController.navigate(Destination.FinishTraining)
    }

    fun clearNavigationPathToHome(){
        CoroutineScope(Dispatchers.Main).launch {
            navHostController.navigate(Destination.Home) {
                popUpTo(navHostController.graph.startDestinationId) {
                    inclusive = false
                }
                launchSingleTop = true
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    //Fungsi buat memanggil model dan mulai training local
    fun startLocalTraining(){
        CoroutineScope(Dispatchers.IO).launch {
            val data = efficientNetB0?.train(
                config = modelConfig,
                dataset = annotateViewModel.datasetManager.getData(),
                onProgressUpdate = { epoch, loss ->
                    println("Progress pelatihan $epoch")
                    currentEpoch.value = epoch+1
                    currentLoss.value = loss
                    if (!loss.isNaN() && !loss.isInfinite()) {
                        lossList.add(loss)
                        updateLossChartData()
                    }

                }
            )

            print("SENDING CLASSIFIER WEIGHT")
            globalModelRepository.uploadModelWeight(globalWeightModel = GlobalWeightModel(
                num_sample = annotateViewModel.datasetManager.getDataSize(),
                label_count = annotateViewModel.datasetManager.getEachLabelCount(),
                weights = encodeWeightsToBase64(data?.get("weights") as Array<FloatArray>),
                bias = floatArrayToBase64(data.getValue("bias") as FloatArray),
                loss = lossList,
                average_loss = lossList.average().toFloat()
            )
            )
            //clearTrainingData()
        }
    }


}