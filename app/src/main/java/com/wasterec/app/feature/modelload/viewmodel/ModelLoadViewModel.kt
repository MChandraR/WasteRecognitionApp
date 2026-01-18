package com.wasterec.app.feature.modelload.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.navigation.NavHostController
import com.wasterec.app.feature.training.viewmodel.TrainingViewModel
import com.wasterec.app.manager.ClassifierWeightFileManager
import com.wasterec.app.manager.FileManager
import com.wasterec.app.model.Destination
import com.wasterec.app.repositories.GlobalModelRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ModelLoadViewModel(
    application : Application,
    val context: Context,
    val navHostController: NavHostController,
    val trainingViewModel: TrainingViewModel
    ) : AndroidViewModel(application) {

    val globalModelDownloadProgress : MutableState<Float> = mutableStateOf(0f)
    val backboneModelDownloadProgress : MutableState<Float> = mutableStateOf(0f)
    val classifierWeightDownloadProgress : MutableState<Float> = mutableStateOf(0f)
    val globalModelRepository : GlobalModelRepository = GlobalModelRepository()
    val fileManager: FileManager = FileManager(context = context)
    val classifierWeightFileManager: ClassifierWeightFileManager = ClassifierWeightFileManager(context)
    //FUngsi untuk download global model dari server sebelum proses anotasi
    fun downloadGlobalModel(){
        globalModelRepository.donwloadGlobalModelFile(
            onResponse = { response ->
                CoroutineScope(Dispatchers.IO).launch {
                    fileManager.saveDownloadFileToDisk(
                        "GlobalModel.ptl",
                        response,
                        onProgress = {
                            globalModelDownloadProgress.value = it
                            CoroutineScope(Dispatchers.Main).launch{
                                navigateToDatasetImport()
                            }                        }
                    )
                }
            },
            onFailure = {

            }
        )
    }

    //Fungsi untuk mendownload backbone model terbaru dari server untuk proses training
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
                    classifierWeightFileManager.saveClassifierParamToFile(
                        result.weights,
                        result.bias
                    )
                }
            }
        }
    }

    fun navigateToDatasetImport(){
        if(globalModelDownloadProgress.value >= 1f && backboneModelDownloadProgress.value >= 1f){
            navHostController.navigate(Destination.Import)
        }
    }
}