package com.wasterec.app.feature.home.viewmodel

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.wasterec.app.helper.GlobalModelError
import com.wasterec.app.manager.FileManager
import com.wasterec.app.manager.JsonFileManager
import com.wasterec.app.model.Destination
import com.wasterec.app.model.api_response.model_info.GlobalModelInfoModel
import com.wasterec.app.model.domain.TrainingData
import com.wasterec.app.model.globalmodel.GlobalWeightModel
import com.wasterec.app.repositories.DatasetUploadRepository
import com.wasterec.app.repositories.GlobalModelRepository
import com.wasterec.app.repositories.TrainingRepository
import com.wasterec.app.services.SharedPreferenceService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class HomeViewModel(
    val navHostController: NavHostController,
    application: Application
) : AndroidViewModel(application = application) {
    val globalModelInfoModel : MutableState<GlobalModelInfoModel?> = mutableStateOf(null)
    val globalModelRepository: GlobalModelRepository = GlobalModelRepository(application.baseContext)
    val sharedPreferenceService = SharedPreferenceService(application.baseContext)
    val datasetRepository = DatasetUploadRepository(application.baseContext)

    val trainingRepository = TrainingRepository(application.baseContext)
    val isTrainingOpenForClient = mutableStateOf(false)
    val isThereRemainDataset = mutableStateOf(true)
    val isUserAlreadyLoggedIn = mutableStateOf(false)



    fun getGlobalModelInfo(){
        globalModelRepository.fetchGlobalModel({ error, result ->
            println("Berhasil mendapatkan data global model")

            if(error==null){
                globalModelInfoModel.value = result
                println("Berhasil mendapatkan data global model ${globalModelInfoModel.value?.model_name}")
            }else{

                CoroutineScope(Dispatchers.Main).launch{
                    if (error is GlobalModelError.Unauthorized){
                        navHostController.navigate(Destination.Login) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                    Toast.makeText(globalModelRepository.context, error.message,  Toast.LENGTH_LONG).show()
                }
                println(error.message)
            }
        })
    }

    fun checkIfTheresDatasetRemain(){
        FileManager(application.baseContext).getFilesFromFolder(File(application.baseContext.filesDir, "dataset")).forEach {
            println(it.path)
            datasetRepository.uploadDatasetToServer(it, onSuccess = {
                it.delete()
                println("Berhasil upload dataset ke server")
            }, onFailed = {
                println("Gagal upload dataset ke server")
            })
        }
    }

    fun sendRemainDatasetToServer(){

    }

    //Fungsi buat check apaah ada training yang belum terkirim
    fun checkRemainTrainingData(){
        viewModelScope.launch {
            val jsonFileManager = JsonFileManager<MutableList<GlobalWeightModel>>(application.baseContext,"PendingTrainingData.json")
            val pendingTrainingDataList = jsonFileManager.loadJsonFile<MutableList<GlobalWeightModel>>()
            isThereRemainDataset.value = pendingTrainingDataList?.isNotEmpty() ?: true

            if(pendingTrainingDataList != null && pendingTrainingDataList.size > 0){
                pendingTrainingDataList.forEachIndexed{ idx,trainignData  ->
                    uploadRemainingTrainingDataToServer(trainignData){
                        println("Hapus 1 data")
                        pendingTrainingDataList.removeAt(idx)
                        if(idx >= pendingTrainingDataList.size ){
                            CoroutineScope(Dispatchers.IO).launch {
                                jsonFileManager.saveJsonFiles(pendingTrainingDataList)
                            }
                        }

                    }
                }

                println("Terdapat data yang pending")
            }else{
                println("Tidak ada data yang pending")
            }
        }

    }

    fun uploadRemainingTrainingDataToServer(globalWeight: GlobalWeightModel, onSucess : ()->Unit){
        globalModelRepository.uploadModelWeight(globalWeight, onSuccess = {
            onSucess()
        })
    }

    fun getTrainingStatusForClient(){
        trainingRepository.getTrainingStatusForClient(
            onSuccess = { trainingStatus ->
                trainingStatus?.let {
                    isTrainingOpenForClient.value = it.isopen
                    println("APakah dibuka ? ${it.isopen}")
                }
            },
            onFailed = {

            }
        )
    }

    fun logout(){
        sharedPreferenceService.removeStringValue("authToken")
    }

    fun checkIfUserLoggedIn(){
        val authToken = sharedPreferenceService.getStringValue("authToken")
        isUserAlreadyLoggedIn.value = ( (authToken!="") && (authToken != null) )
    }

}