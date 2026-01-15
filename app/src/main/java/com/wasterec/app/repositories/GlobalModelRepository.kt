package com.wasterec.app.repositories

import com.wasterec.app.helper.AppError
import com.wasterec.app.helper.GlobalModelError
import com.wasterec.app.model.globalmodel.GlobalModelInfoModel
import com.wasterec.app.model.globalmodel.GlobalModelWeightModel
import com.wasterec.app.model.globalmodel.GlobalWeightModel
import com.wasterec.app.services.ApiService
import com.wasterec.app.services.GlobalModelService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GlobalModelRepository : ApiService() {
    fun getGlobamModelService() : GlobalModelService?{
       return this.retrofit?.create(GlobalModelService::class.java)
    }

    //Function to fetch data related to Global Model Info from server
    fun fetchGlobalModel(callback : (exception : AppError?, result : GlobalModelInfoModel?) -> Unit) {
        val fetchGlobalModelService = this.getGlobamModelService()
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

    //Fungsi to fetch model weight from server
    fun fetchGlobalModelWeight(callback : (exception : AppError?, result : GlobalModelWeightModel?)->Unit){
        val globalModelService = this.getGlobamModelService()
        if (globalModelService != null) {
            CoroutineScope(Dispatchers.IO).launch{
                val response = globalModelService.getModelWeight()
                if(response.isSuccessful){
                    callback(null, response.body())
                }else{
                    callback(GlobalModelError.NoInternet(), null)
                }
            }
        }
    }


    fun uploadModelWeight(globalWeightModel: GlobalWeightModel){
        if(this.getGlobamModelService() != null){
            val globalModelService = this.getGlobamModelService()

            CoroutineScope(Dispatchers.IO).launch {
                val response = globalModelService?.updateModelWeight(globalWeight = globalWeightModel)
                if (response != null) {
                    if(response.isSuccessful){
                        println("response ${response.body().toString()}")
                    }else{
                        println("Gagal ${response.errorBody().toString()} ${response.body().toString()}")
                    }
                }
            }
        }
    }
}