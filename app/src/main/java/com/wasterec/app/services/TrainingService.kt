package com.wasterec.app.services

import com.wasterec.app.model.api_response.ResponseBody
import com.wasterec.app.model.api_response.training.TrainingStatusResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET

interface TrainingService {
    @GET("training/status")
    suspend fun getTrainingStatus(): Response<ResponseBody<TrainingStatusResponse>>
}