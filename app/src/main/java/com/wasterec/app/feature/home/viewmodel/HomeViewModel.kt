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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(
    val navHostController: NavHostController,
    application: Application
) : AndroidViewModel(application = application) {
    val globalModelInfoModel : MutableState<GlobalModelInfoModel?> = mutableStateOf(null)
    val globalModelRepository: GlobalModelRepository = GlobalModelRepository(application.baseContext)

    val datasetUploadRepository = DatasetUploadRepository(application.baseContext)

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

        //datasetUploadRepository.uploadDatasetToServer()
    }


}