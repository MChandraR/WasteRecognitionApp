package com.wasterec.app.services

import android.content.Context
import com.wasterec.app.utils.AuthInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


open class ApiService(
    val baseUrl : String = "http://192.168.1.11:8000/",
    var retrofit: Retrofit? = null,
    val authToken : String? = null,
){
    init {
        val retrofit  =  Retrofit.Builder().baseUrl(baseUrl)
        authToken?.let{ token ->
            val authInterceptor = AuthInterceptor(token)
            val client = OkHttpClient.Builder().addInterceptor(authInterceptor).build()
            retrofit.client(client)
        }
        retrofit.addConverterFactory(GsonConverterFactory.create()).build()
    }

}