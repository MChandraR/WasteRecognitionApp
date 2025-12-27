package com.wasterec.app.services

import com.wasterec.app.interfaces.LoginInterfaceAPI
import com.wasterec.app.model.LoginModelAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


open class ApiService(
    val baseUrl : String,
    var retrofit: Retrofit? = null
){
    init {
        this.retrofit =  Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).build()
    }

}