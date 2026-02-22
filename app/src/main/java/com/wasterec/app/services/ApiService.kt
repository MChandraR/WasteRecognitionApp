package com.wasterec.app.services

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


open class ApiService(
    val baseUrl : String = "http://192.168.1.11:8000/",
    var retrofit: Retrofit? = null,
){
    init {
        this.retrofit =  Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).build()

    }

}