package com.wasterec.app.feature.training.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.os.BatteryManager
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.wasterec.app.feature.anotate.viewmodel.AnnotateViewModel
import com.wasterec.app.helper.InternalServerErrorException
import com.wasterec.app.helper.NoConnectivityException
import com.wasterec.app.manager.ClassifierWeightFileManager
import com.wasterec.app.manager.DatasetManager
import com.wasterec.app.manager.EfficientNetB0
import com.wasterec.app.manager.FileManager
import com.wasterec.app.manager.JsonFileManager
import com.wasterec.app.model.ClassifierWeightModel
import com.wasterec.app.model.Destination
import com.wasterec.app.model.ModelConfiguration
import com.wasterec.app.model.globalmodel.GlobalWeightModel
import com.wasterec.app.repositories.DatasetUploadRepository
import com.wasterec.app.repositories.GlobalModelRepository
import com.wasterec.app.utils.encodeWeightsToBase64
import com.wasterec.app.utils.floatArrayToBase64
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import kotlin.math.abs

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
    val backgroundLossList = mutableStateListOf<Float>()
    val classifierWeightFileManager : ClassifierWeightFileManager = ClassifierWeightFileManager(app.baseContext)
    val totalDatasetCount: MutableIntState = mutableIntStateOf(0)
    val datasetRepository : DatasetUploadRepository = DatasetUploadRepository(app.baseContext){handleAPIException(it)}
    val fileManager : FileManager = FileManager(app.baseContext)
    val totalLabelCount : MutableList<Int> = mutableListOf(0,0,0,0,0,0)
    val label = arrayOf("Plastik", "Kertas", "Kaca",  "Logam", "Kardus", "Sampah")
    val modelAccuracy = mutableIntStateOf(0)
    val globalModelRepository = GlobalModelRepository(app.baseContext) { handleExceptionAPI() }
    var isTraining = mutableStateOf(false)
    var listOfPendingTrainingData = mutableListOf<GlobalWeightModel>()

    var modelConfig = mutableStateOf(ModelConfiguration(
        learningRate = 0.01f,
        epoch = 30,
        batchSize = 16
    )
    )


    fun getModelAccuracy():Int{
        val accuracy = annotateViewModel.rightLabelCount.intValue.toFloat() / annotateViewModel.totalDataCount.intValue
            .toFloat()
        return (accuracy * 100).toInt()
    }

    //Fungsi untuk reinit nilai atau reset variabel
    @RequiresApi(Build.VERSION_CODES.O)
    fun reInit(){
        currentEpoch.value = 0
        listOfPendingTrainingData.clear()
        modelProducer.value =  CartesianChartModelProducer()
        modelAccuracy.intValue = getModelAccuracy()
        totalDatasetCount.intValue = datasetManager.value.getDataSize()
        datasetManager.value.getEachLabelCount().forEachIndexed { idx, value ->
            totalLabelCount[idx] = value
        }

        val backboneModelFile = File(app.baseContext.filesDir, "Backbone.ptl")
        if(backboneModelFile.exists()){
            efficientNetB0 = EfficientNetB0(app.baseContext, "Backbone.ptl")
        }

        CoroutineScope(Dispatchers.IO).launch {
            val newClassifierParam : ClassifierWeightModel? = classifierWeightFileManager.loadClassifierParamFromFile()
            newClassifierParam?.let{ newParam ->
                efficientNetB0?.setClassifierWeightAndBias(newParam.weights, newParam.bias)
            }
            startLocalTraining()
            modelProducer.value.runTransaction {

            }
        }

        //UPload dataset ke server
        CoroutineScope(Dispatchers.IO).launch{
            val dataset : List<Bitmap> = annotateViewModel.datasetManager.value.getData().map { it.Input }
            dataset.toList().chunked(25).forEachIndexed{ it , data ->
                val datasetToUpload = fileManager.convertBitmapToZipFile(data, File(app.baseContext.cacheDir, "Dataset${it}.zip") )
                datasetToUpload?.let {
                    datasetRepository.uploadDatasetToServer(it)
                    println("Berhasil upload dataset ke server")
                }
            }

        }
        backgroundLossList.clear()
        lossList.clear()

    }

    fun savePendingTrainingData(globalWeight : List<GlobalWeightModel>){
        viewModelScope.launch {
            val jsonFileManager = JsonFileManager<MutableList<GlobalWeightModel>>(
                app.baseContext,
                "PendingTrainingData.json"
            )

            val listOfPendingTrainingData : MutableList<GlobalWeightModel> =
                jsonFileManager.loadJsonFile<MutableList<GlobalWeightModel>>() ?: mutableListOf()

            listOfPendingTrainingData.addAll(globalWeight)
            jsonFileManager.saveJsonFiles(listOfPendingTrainingData)
            listOfPendingTrainingData.clear()
        }

    }

    fun storeUnSentDataset(){
        println("Menyimpan dataset ke offline folder")
        val dataset : List<Bitmap> = annotateViewModel.datasetManager.value.getData().map { it.Input }
        dataset.toList().chunked(25).forEachIndexed { idx, data ->
            fileManager.convertBitmapToZipFile(data, File(app.baseContext.dataDir, "dataset/${System.currentTimeMillis()}_${idx}.zip") )
        }
    }

    fun handleAPIException(e: Exception){
        when(e){
            is InternalServerErrorException -> {
                storeUnSentDataset()
            }
            is NoConnectivityException -> {
                storeUnSentDataset()
            }
            else -> {

            }
        }
    }

    fun updateLossChartData(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                modelProducer.value.runTransaction {
                    lineSeries {
                        // Vico menerima List untuk X dan List untuk Y
                        series(
                            x = lossList.indices.toList(), // x = 0, 1, 2, ...
                            y = lossList                   // y = nilai loss
                        )
                    }
                }
            }catch (e : Exception){
                println("Error updating chart data : ${e.message}")
            }
        }
    }

    fun clearTrainingData(){
        annotateViewModel.datasetManager.value.clearAlLData()
        annotateViewModel.trainingData.clear()
    }

    fun navigateToFinishTrainingView(){
        clearTrainingData()
        navHostController.navigate(Destination.FinishTraining)
    }

    fun clearNavigationPathToHome(){
        if(!isTraining.value) {
            CoroutineScope(Dispatchers.Main).launch {
                navHostController.navigate(Destination.Home) {
                    popUpTo(Destination.Home) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        }else{
            Toast.makeText(app.baseContext, "Harap tunggu proses training selesai", Toast.LENGTH_LONG).show()
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    //Fungsi buat memanggil model dan mulai training local
    fun startLocalTraining(){
        isTraining.value = true
        CoroutineScope(Dispatchers.IO).launch {
            val startTrainingTime = System.currentTimeMillis()
            datasetManager.value.lockTrainingDataFromPreprocessing = true
            val batteryManager = application.baseContext.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            val energyUsageStart = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
            val listOfMemoryUsage = mutableListOf<Long>()
            val listOfEnergyUsage = mutableListOf<Long>()
            efficientNetB0?.loadClassifierParams {
                efficientNetB0?.setClassifierWeight(it.first)
                efficientNetB0?.setClassifierBias(it.second)
            }
            efficientNetB0?.train(
                config = modelConfig.value,
                dataset = annotateViewModel.datasetManager.value.getData(),
                onProgressUpdate = { epoch, loss ->

                    val currentMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
                    listOfMemoryUsage.add(currentMemory)
                    val currentEnergyUsage = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)

                    listOfEnergyUsage.add(abs(currentEnergyUsage - energyUsageStart))
                    backgroundLossList.add(loss)

                    viewModelScope.launch {
                        currentEpoch.value = epoch + 1
                        currentLoss.value = loss
                        if (!loss.isNaN()) {
                            lossList.add(loss)
                            updateLossChartData()
                        }

                    }

                },
                onFinished = { it, data ->
                    datasetManager.value.lockTrainingDataFromPreprocessing = false
                    modelConfig.value.epoch = it
                    currentEpoch.value = it


                    isTraining.value = false
                    val endTrainingTime = System.currentTimeMillis()


                    print("SENDING CLASSIFIER WEIGHT")
                    val newGlobalWeight = GlobalWeightModel(
                        num_sample = annotateViewModel.datasetManager.value.getDataSize(),
                        label_count = annotateViewModel.datasetManager.value.getEachLabelCount(),
                        weights = encodeWeightsToBase64(data?.get("weights") as Array<FloatArray>),
                        bias = floatArrayToBase64(data.getValue("bias") as FloatArray),
                        loss = backgroundLossList,
                        last_loss = currentLoss.value,
                        training_time =  endTrainingTime - startTrainingTime,
                        memory_usage = listOfMemoryUsage.toList(),
                        energy_usage = listOfEnergyUsage.toList()
                    )


                    CoroutineScope(Dispatchers.Main).launch {
                        listOfPendingTrainingData.add(newGlobalWeight)
                    }
                    globalModelRepository.uploadModelWeight(globalWeightModel = newGlobalWeight,
                        onFailed = {
                            println("Gagal menupload param training")
                        },
                        onSuccess = {
                            println("Berhasil mengunggah training data ke server !!")
                        }
                    )
                }
            )


            //clearTrainingData()
        }
    }

    fun handleExceptionAPI(){
        savePendingTrainingData(listOfPendingTrainingData)
    }


}