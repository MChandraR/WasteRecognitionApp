package com.wasterec.app.repositories

import android.content.Context
import com.wasterec.app.helper.ExpiredAuthTokenException
import com.wasterec.app.model.api_response.training.TrainingDataResponse
import com.wasterec.app.model.api_response.training.TrainingStatusResponse
import com.wasterec.app.model.domain.TrainingData
import com.wasterec.app.services.ApiService
import com.wasterec.app.services.SharedPreferenceService
import com.wasterec.app.services.TrainingService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TrainingRepository(val context: Context, val onException : (e : Exception) -> Unit = {}): ApiService() {
    private fun getTrainingService(request : suspend (trainingService : TrainingService)->Unit){
        val authToken = SharedPreferenceService(context).getStringValue("authToken")
        authToken?.let{addAuthorizationBerer(authToken)}

        this.retrofit?.create(TrainingService::class.java)?.let{ trainingService ->
            CoroutineScope(Dispatchers.IO).launch {
                try{
                    request(trainingService)
                }catch (e: Exception){
                    onException(e)
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

    fun getTrainingDataHistoryForClient(onSuccess : (data: List<TrainingData>)->Unit, onFailed : (message : String)->Unit ){
        this.getTrainingService { trainingService ->
            val response = trainingService.getTrainingData()

            if (response.isSuccessful) {
                response.body()?.data?.let {
                    onSuccess(it.map { TrainingData(it.session_id, it.user_id, it.weight_id, it.num_data, it.label_count, it.created_at, it.status, it.loss, it.last_loss) })
                }
            } else {
                println("Response : ${response.code()}")
                onFailed(response.message())
                if(response.code() == 401){
                    onException(ExpiredAuthTokenException("Token Expired"))
                }
            }
        }
    }
}