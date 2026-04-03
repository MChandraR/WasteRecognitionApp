package com.wasterec.app.utils

import com.wasterec.app.helper.NoConnectivityException
import okhttp3.Interceptor
import okhttp3.Response
import java.net.ConnectException
import java.net.SocketTimeoutException

class AuthInterceptor(private val token: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        try {
            println("Inject token bearer with token : $token")
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            return chain.proceed(request)
        } catch (e: Exception) {
            when(e){
                else -> {
                    throw e
                }
            }
        }
    }
}