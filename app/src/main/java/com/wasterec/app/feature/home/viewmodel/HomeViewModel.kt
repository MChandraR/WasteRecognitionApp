package com.wasterec.app.feature.home.viewmodel

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.wasterec.app.model.api_response.model_info.GlobalModelInfoModel
import com.wasterec.app.repositories.GlobalModelRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(
    val context: Context,
    val navHostController: NavHostController,
    application: Application
) : AndroidViewModel(application = application) {
    val globalModelInfoModel : MutableState<GlobalModelInfoModel?> = mutableStateOf(null)
    val globalModelRepository: GlobalModelRepository = GlobalModelRepository()
    fun getGlobalModelInfo(){
        globalModelRepository.fetchGlobalModel({ error, result ->
            if(error==null){
                globalModelInfoModel.value = result
                println("Berhasil mendapatkan data global model")
            }else{
                CoroutineScope(Dispatchers.Main).launch{
                    Toast.makeText(context, error.message,  Toast.LENGTH_LONG).show()
                }
                println(error.message)
            }
        })
    }


}