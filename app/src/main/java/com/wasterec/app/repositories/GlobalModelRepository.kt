package com.wasterec.app.repositories

import android.content.Context
import com.wasterec.app.helper.AppError
import com.wasterec.app.helper.ExpiredAuthTokenException
import com.wasterec.app.helper.GlobalModelError
import com.wasterec.app.helper.InternalServerErrorException
import com.wasterec.app.model.globalmodel.ClassifierWeightModel
import com.wasterec.app.model.api_response.model_info.GlobalModelInfoModel
import com.wasterec.app.model.globalmodel.GlobalWeightModel
import com.wasterec.app.services.ApiService
import com.wasterec.app.services.GlobalModelService
import com.wasterec.app.services.SharedPreferenceService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody

class GlobalModelRepository(val context: Context? = null, val onException : (exception : Exception)->Unit = {}) : ApiService() {
    fun getGlobamModelService(request : suspend (globalModelService : GlobalModelService)->Unit) {
        //If there's context get stored AuthToken from SP and inject to Request Header
        context?.let{
            val authToken = SharedPreferenceService(context).getStringValue("authToken")
            authToken?.let{addAuthorizationBerer(authToken)}
        }
       this.retrofit?.create(GlobalModelService::class.java)?.let {
           CoroutineScope(Dispatchers.IO).launch {
               try{
                   request(it)
               }catch (e : Exception){
                    onException(e)
                    println("Exception ${e.toString()}")
               }
           }
       }
    }

    //Function to fetch data related to Global Model Info from server
    fun fetchGlobalModel(callback : (exception : AppError?, result : GlobalModelInfoModel?) -> Unit) {
        this.getGlobamModelService{
            val response = it.getModelInfo()
            if(response.isSuccessful){
                if(response.body()?.status == 401)  callback(GlobalModelError.Unauthorized(), null)
                else callback(null, response.body()?.data)
            }else{
                if(response.code() == 401)  callback(GlobalModelError.Unauthorized(), null)
            }

        }
    }

    //Fungsi to fetch model weight from server
    fun fetchGlobalModelClassifierWeight(callback : (exception : AppError?, result : ClassifierWeightModel?)->Unit){
        this.getGlobamModelService{
            val response = it.getClassifierModelWeight()
            if(response.isSuccessful){
                if(response.body()?.status == 400)  callback(GlobalModelError.Unauthorized(), null)
                else callback(null, response.body()?.data)
            }else{
                if(response.code() == 401){
                    onException(ExpiredAuthTokenException("Token Expired"))
                }
                callback(GlobalModelError.NoInternet(), null)
            }
        }
    }

    fun uploadModelWeight(globalWeightModel: GlobalWeightModel,
                          onSuccess : ()->Unit = {},
                          onFailed : (message : String)->Unit = {}
                          ){
        this.getGlobamModelService{
            val response = it.updateModelWeight(globalWeight = globalWeightModel)
            if(response.isSuccessful){
                println("response ${response.body().toString()}")
                onSuccess()
            }else{
                onException(InternalServerErrorException("Token Expired"));
                onFailed(response.errorBody().toString())
                println("Gagal ${response.errorBody().toString()} ${response.body().toString()}")
            }
        }
    }

    fun donwloadGlobalModelFile(onResponse : (response : ResponseBody)->Unit, onFailure : (message:String)->Unit){
        this.getGlobamModelService {
            val downloadResponse = it.downloadGlobalModelStream()
            if (downloadResponse.body() != null) {
                onResponse(downloadResponse.body()!!)
            } else {
                onFailure(downloadResponse.message().toString())
            }
        }
    }

    fun donwloadBackboneModelFile(onResponse : (response : ResponseBody)->Unit, onFailure : (message:String)->Unit){
        this.getGlobamModelService {
            val downloadResponse = it.downloadBackboneModelStream()
            if (downloadResponse.body() != null) {
                onResponse(downloadResponse.body()!!)
            } else {
                if(downloadResponse.code() == 401){
                    onException(ExpiredAuthTokenException("Token Expired"))
                }
                onFailure(downloadResponse.message().toString())
            }

        }
    }
}