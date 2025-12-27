package com.wasterec.app.services

import android.accounts.NetworkErrorException
import com.wasterec.app.helper.LoginAPIError
import com.wasterec.app.interfaces.LoginInterfaceAPI
import com.wasterec.app.model.LoginModelAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginAPIService : ApiService("http://192.168.1.4:5001") {
    //Function to send credential to login endpoint
    fun login(loginBody : LoginModelAPI) {
        var loginAPIInterface = retrofit?.create(LoginInterfaceAPI::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            if(loginAPIInterface != null){
                var loginResponse = loginAPIInterface.login(credential = loginBody)
                if(loginResponse.isSuccessful){
                    println("Sucessfully login with message ${loginResponse.message()}")
                }else{
                    println("Failed to login cause error : ${loginResponse.errorBody().toString()}")
                }
            }
        }
    }
}