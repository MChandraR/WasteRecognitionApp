package com.wasterec.app.services

import com.wasterec.app.model.LoginModelAPI
import com.wasterec.app.model.TrainingModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {

    @POST("login")
    suspend fun login(@Body credential : LoginModelAPI): Response<TrainingModel>
}