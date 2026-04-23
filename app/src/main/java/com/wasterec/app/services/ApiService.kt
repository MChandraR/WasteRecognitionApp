package com.wasterec.app.services

import com.wasterec.app.utils.AuthInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


open class ApiService(
    val baseUrl : String = "https://tsb2hcg2t2e4mwz3conksbihui0xgdlq.lambda-url.ap-southeast-1.on.aws//api/",
    var retrofit: Retrofit? = null,
    val authToken : String? = null,
){
    init {
        retrofit =  Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).build()
        authToken?.let{
            addAuthorizationBerer(authToken = authToken)
        }
    }

    fun addAuthorizationBerer(authToken : String){
        val retrofit  =  Retrofit.Builder().baseUrl(baseUrl)
        authToken.let{ token ->
            val authInterceptor = AuthInterceptor(token)
            val client = OkHttpClient.Builder().addInterceptor(authInterceptor).build()
            retrofit.client(client)
        }
        retrofit.addConverterFactory(GsonConverterFactory.create())
        this.retrofit = retrofit.build()
    }

}