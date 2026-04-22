package com.wasterec.app.services

import com.wasterec.app.model.api_request.LoginModelRequestAPI
import com.wasterec.app.model.api_response.ResponseBody
import com.wasterec.app.model.api_response.login.LoginResponseModelAPI
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {

    @POST("auth/login")
    suspend fun login(@Body credential : LoginModelRequestAPI): Response<ResponseBody<LoginResponseModelAPI>>
}