package com.wasterec.app.feature.modelload.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.navigation.NavHostController
import com.wasterec.app.manager.ClassifierWeightFileManager
import com.wasterec.app.manager.FileManager
import com.wasterec.app.model.Destination
import com.wasterec.app.model.api_response.model_info.GlobalModelInfoModel
import com.wasterec.app.repositories.GlobalModelRepository
import com.wasterec.app.services.SharedPreferenceService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class ModelLoadViewModel(
    application : Application,
    val context: Context,
    val navHostController: NavHostController,
    ) : AndroidViewModel(application) {

    val backboneModelDownloadProgress : MutableState<Float> = mutableFloatStateOf(0f)
    val classifierWeightDownloadProgress : MutableState<Float> = mutableFloatStateOf(0f)
    val globalModelRepository : GlobalModelRepository = GlobalModelRepository(context)
    val fileManager: FileManager = FileManager(context = context)
    val sharedPreferenceService = SharedPreferenceService(application.baseContext)
    val classifierWeightFileManager: ClassifierWeightFileManager = ClassifierWeightFileManager(context)
    //FUngsi untuk download global model dari server sebelum proses anotasi

    //Fungsi untuk mendownload backbone model terbaru dari server untuk proses training

    fun getGlobalModel(){
        globalModelRepository.fetchGlobalModel({ error, result ->
            val modelVersion = sharedPreferenceService.getStringValue("model_version")
            val isFileExist = File(context.filesDir, "Backbone.ptl").exists() && File(context.filesDir, "classifier_param.json").exists()
            println("Model Version : $modelVersion")
            result?.model_version?.let{
                sharedPreferenceService.storeStringValue("model_version", it)
            }
            if( modelVersion == null || modelVersion != result?.model_version || !isFileExist){
                downloadBackboneModel()
                loadClassifierParam()
            }else{
                navHostController.navigate(Destination.ImportDataset)
            }
        })
    }

    fun downloadBackboneModel(){
        globalModelRepository.donwloadBackboneModelFile(
            onResponse = { response->
                CoroutineScope(Dispatchers.IO).launch{
                    fileManager.saveDownloadFileToDisk(
                        "Backbone.ptl",
                        responseBody = response,
                        onProgress = {
                            backboneModelDownloadProgress.value = it
                            CoroutineScope(Dispatchers.Main).launch{
                                navigateToDatasetImport()
                            }
                        }
                    )
                }
            },
            onFailure = {

            }
        )
    }

    //Fungsi untuk mendownload bobot layer classifer terbaru dari sever untuk digunakan saat proses training
    fun loadClassifierParam(){
        globalModelRepository.fetchGlobalModelClassifierWeight { exception, result ->
            if(result != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    classifierWeightDownloadProgress.value = 1f
                    //println("Size ${result.weights.size} x ${result.weights.get(0).size}")
                    classifierWeightFileManager.saveClassifierParamToFile(
                        result.weights,
                        result.bias
                    )
                }
            }
        }
    }

    fun navigateToDatasetImport(){
        if(backboneModelDownloadProgress.value >= 1f){
            navHostController.navigate(Destination.ImportDataset)
        }
    }
}