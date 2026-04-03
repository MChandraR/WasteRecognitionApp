package com.wasterec.app.repositories

import android.content.Context
import com.wasterec.app.model.api_response.training.TrainingStatusResponse
import com.wasterec.app.services.ApiService
import com.wasterec.app.services.SharedPreferenceService
import com.wasterec.app.services.TrainingService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TrainingRepository(val context: Context): ApiService() {
    private fun getTrainingService(request : suspend (trainingService : TrainingService)->Unit){
        val authToken = SharedPreferenceService(context).getStringValue("authToken")
        authToken?.let{addAuthorizationBerer(authToken)}

        this.retrofit?.create(TrainingService::class.java)?.let{ trainingService ->
            CoroutineScope(Dispatchers.IO).launch {
                try{
                    request(trainingService)
                }catch (e: Exception){
                    println("Training Service Exception : ${e.message}")
                }
            }
        }
    }

    fun getTrainingStatusForClient(onSuccess : (data : TrainingStatusResponse?)->Unit, onFailed : (message : String)->Unit){
        this.getTrainingService { trainingService ->
            val response = trainingService.getTrainingStatus()

            if(response.isSuccessful){
                response.body()?.data.let {
                    onSuccess(it)
                }
            }else{
                println("Response : ${response.errorBody()}")
                onFailed(response.message())
            }
        }
    }
}