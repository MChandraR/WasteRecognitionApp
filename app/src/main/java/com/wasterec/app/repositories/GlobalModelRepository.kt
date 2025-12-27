package com.wasterec.app.repositories

import com.wasterec.app.helper.AppError
import com.wasterec.app.helper.GlobalModelError
import com.wasterec.app.model.globalmodel.GlobalModelInfoModel
import com.wasterec.app.services.ApiService
import com.wasterec.app.services.GlobalModelService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GlobalModelRepository : ApiService(baseUrl = "http://192.168.1.4:5001") {
    fun fetchGlobalModel(callback : (exception : AppError?, result : GlobalModelInfoModel?) -> Unit) {
        val fetchGlobalModelService = this.retrofit?.create(GlobalModelService::class.java)
        if (fetchGlobalModelService != null){
            CoroutineScope(Dispatchers.IO).launch {
                val response = fetchGlobalModelService.getModelInfo()
                if(response.isSuccessful){
                     callback(null, response.body())
                }else{
                    callback(GlobalModelError.NoInternet(), null)
                }
            }
        }

    }
}