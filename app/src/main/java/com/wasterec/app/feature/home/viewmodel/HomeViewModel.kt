package com.wasterec.app.feature.home.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.wasterec.app.model.globalmodel.GlobalModelInfoModel
import com.wasterec.app.repositories.GlobalModelRepository

class HomeViewModel(
    val globalModelInfoModel : MutableState<GlobalModelInfoModel?> = mutableStateOf(null),
    val globalModelRepository: GlobalModelRepository = GlobalModelRepository()
) : ViewModel() {
    fun getGlobalModelInfo(){
        globalModelRepository.fetchGlobalModel({ error, result ->
            if(error==null){
                globalModelInfoModel.value = result
                println("Berhasil mendapatkan data global model")
            }else{
                println(error.message)
            }
        })
    }


}