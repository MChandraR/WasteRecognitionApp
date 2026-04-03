package com.wasterec.app.utils

import com.wasterec.app.helper.NoConnectivityException
import okhttp3.Interceptor
import okhttp3.Response
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException

class NetworkErrorInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        try {
            return chain.proceed(chain.request())
        } catch (e: Exception) {
            when(e){
                is NoConnectivityException ->{
                    return chain.proceed(chain.request())
                }
                is ConnectException -> {
                    println("Cannot connect to server Exception")
                    return chain.proceed(chain.request())                }
                is SocketTimeoutException -> {
                    println("Socket time out Exception")
                    return chain.proceed(chain.request())                }

                else -> {
                    throw e
                }
            }
        }
    }
}