package com.wasterec.app.feature.home.viewmodel

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.navigation.NavHostController
import com.wasterec.app.helper.GlobalModelError
import com.wasterec.app.model.Destination
import com.wasterec.app.model.api_response.model_info.GlobalModelInfoModel
import com.wasterec.app.repositories.DatasetUploadRepository
import com.wasterec.app.repositories.GlobalModelRepository
import com.wasterec.app.repositories.TrainingRepository
import com.wasterec.app.services.SharedPreferenceService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(
    val navHostController: NavHostController,
    application: Application
) : AndroidViewModel(application = application) {
    val globalModelInfoModel : MutableState<GlobalModelInfoModel?> = mutableStateOf(null)
    val globalModelRepository: GlobalModelRepository = GlobalModelRepository(application.baseContext)
    val sharedPreferenceService = SharedPreferenceService(application.baseContext)

    val trainingRepository = TrainingRepository(application.baseContext)
    val isTrainingOpenForClient = mutableStateOf(false)
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