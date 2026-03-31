package com.wasterec.app.services

import com.wasterec.app.utils.AuthInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


open class ApiService(
    val baseUrl : String = "http://192.168.1.8:8000/",
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