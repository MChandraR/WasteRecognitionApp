package com.wasterec.app.interfaces

import com.wasterec.app.model.LoginModelAPI
import com.wasterec.app.model.TrainingModel
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface LoginInterfaceAPI {

    @POST("login")
    suspend fun login(@Body credential : LoginModelAPI): Response<TrainingModel>
}