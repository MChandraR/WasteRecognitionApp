package com.wasterec.app.repositories

import com.wasterec.app.services.UserService
import com.wasterec.app.model.api_request.LoginModelRequestAPI
import com.wasterec.app.model.api_response.ResponseBody
import com.wasterec.app.model.api_response.login.LoginResponseModelAPI
import com.wasterec.app.services.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserRepository : ApiService() {
    //Function to send credential to login endpoint
    fun login(loginBody : LoginModelRequestAPI, onSuccess: (responseBody : ResponseBody<LoginResponseModelAPI>)->Unit, onFailed: (message:String)->Unit) {
        var loginAPIInterface = retrofit?.create(UserService::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            if(loginAPIInterface != null){
                var loginResponse = loginAPIInterface.login(credential = loginBody)
                if(loginResponse.isSuccessful ){
                    loginResponse.body()?.let{
                        if (it.status == 200)onSuccess(it)
                        else onFailed(it.message)
                    }
                    println("Sucessfully login with message ${loginResponse.message()}")
                }else{
                    onFailed(loginResponse.message())
                    println("Failed to login cause error : ${loginResponse.errorBody().toString()}")
                }
            }
        }
    }
}