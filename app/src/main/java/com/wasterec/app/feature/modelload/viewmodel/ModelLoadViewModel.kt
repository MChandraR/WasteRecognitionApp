package com.wasterec.app.feature.modelload.viewmodel

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.navigation.NavHostController
import com.wasterec.app.helper.ExpiredAuthTokenException
import com.wasterec.app.manager.ClassifierWeightFileManager
import com.wasterec.app.manager.FileManager
import com.wasterec.app.model.Destination
import com.wasterec.app.repositories.GlobalModelRepository
import com.wasterec.app.services.SharedPreferenceService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File

class ModelLoadViewModel(
    application : Application,
    val context: Context,
    val navHostController: NavHostController,
    ) : AndroidViewModel(application) {

    val backboneModelDownloadProgress : MutableState<Float> = mutableFloatStateOf(0f)
    val classifierWeightDownloadProgress : MutableState<Float> = mutableFloatStateOf(0f)
    val globalModelRepository : GlobalModelRepository = GlobalModelRepository(context, {handleAPIException(it)})
    val fileManager: FileManager = FileManager(context = context)
    val sharedPreferenceService = SharedPreferenceService(application.baseContext)
    val classifierWeightFileManager: ClassifierWeightFileManager = ClassifierWeightFileManager(context)
    //FUngsi untuk download global model dari server sebelum proses anotasi

    //Fungsi untuk mendownload backbone model terbaru dari server untuk proses training
    var globalModelDownloadTask : Job? = null
    var classifierWeightDownloadTask : Job? = null
    var modelVersion = ""

    fun cancelDownload(){
        globalModelDownloadTask?.cancel()
        classifierWeightDownloadTask?.cancel()
    }


    fun getGlobalModel(){
        globalModelRepository.fetchGlobalModel({ error, result ->
            val modelVersion = sharedPreferenceService.getStringValue("model_version")
            val isFileExist = File(context.filesDir, "Backbone.ptl").exists() && File(context.filesDir, "classifier_param.json").exists()
            println("Model Version : $modelVersion")

            if( (modelVersion == null || modelVersion != result?.model_version ) || !isFileExist){
                result?.model_version?.let{
                    this.modelVersion = it
                }
                downloadBackboneModel()
                loadClassifierParam()
            }else{
                CoroutineScope(Dispatchers.Main).launch{
                    if(navHostController.currentDestination?.route.toString() == "com.wasterec.app.model.Destination.WeightLoadingForClassification") navHostController.navigate(Destination.Classify)
                    else navHostController.navigate(Destination.ImportDataset)
                }
            }
        })
    }

    fun updateModelVersion(modelVersion : String){
        sharedPreferenceService.storeStringValue("model_version", modelVersion)

    }

    fun downloadBackboneModel(){
        if(!(globalModelDownloadTask?.isActive ?: false)){
            globalModelRepository.donwloadBackboneModelFile(
                onResponse = { response->
                    globalModelDownloadTask = CoroutineScope(Dispatchers.IO).launch{
                        fileManager.saveDownloadFileToDisk(
                            "Backbone.ptl",
                            responseBody = response,
                            onProgress = {
                                backboneModelDownloadProgress.value = it

                                if (checkIfBothDownloadProgressIsComplete()){
                                    updateModelVersion(modelVersion)
                                    CoroutineScope(Dispatchers.Main).launch{
                                        navigateToNextPage()
                                    }
                                }
                            }
                        )
                    }
                },
                onFailure = {

                }
            )
        }
    }

    //Fungsi untuk mendownload bobot layer classifer terbaru dari sever untuk digunakan saat proses training
    fun loadClassifierParam(){
        if(!(classifierWeightDownloadTask?.isActive ?: false)){
            globalModelRepository.fetchGlobalModelClassifierWeight { exception, result ->
                if(result != null) {
                    classifierWeightDownloadTask = CoroutineScope(Dispatchers.IO).launch {
                        classifierWeightDownloadProgress.value = 1f
                        //println("Size ${result.weights.size} x ${result.weights.get(0).size}")
                        classifierWeightFileManager.saveClassifierParamToFile(
                            result.weights,
                            result.bias
                        )
                        if(checkIfBothDownloadProgressIsComplete()){
                            updateModelVersion(modelVersion)
                            CoroutineScope(Dispatchers.Main).launch{
                                navigateToNextPage()
                            }
                        }
                    }
                }
            }
        }
    }

    fun checkIfBothDownloadProgressIsComplete() : Boolean{
        return backboneModelDownloadProgress.value >= 1f && classifierWeightDownloadProgress.value >= 1f
    }

    fun navigateToNextPage(){
        println("${
            navHostController.currentDestination?.route?.split(".")?.get(5)
        } == ${Destination.WeightLoading.toString()}")
        if(backboneModelDownloadProgress.value >= 1f && navHostController.currentDestination?.route?.split(".")?.get(5) == Destination.WeightLoading.toString()){

            if(navHostController.currentDestination?.route.toString() == "com.wasterec.app.model.Destination.WeightLoadingForClassification") navHostController.navigate(Destination.Classify)
            else navHostController.navigate(Destination.ImportDataset)
        }
    }

    fun navigateToHome(){
        navHostController.navigate(Destination.Home) {
            popUpTo(navHostController.graph.startDestinationId) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    fun handleAPIException(e: Exception){
        when(e){
            is ExpiredAuthTokenException -> {
                CoroutineScope(Dispatchers.Main).launch{
                    Toast.makeText(application.baseContext, "Sesi telah kadaluarsa silahkan login kembali !", Toast.LENGTH_SHORT).show()
                    navigateToHome()
                }

            }
            else -> {

            }
        }
    }
}