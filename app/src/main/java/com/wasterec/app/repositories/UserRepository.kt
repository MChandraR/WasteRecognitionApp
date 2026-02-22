package com.wasterec.app.repositories

import com.wasterec.app.services.UserService
import com.wasterec.app.model.LoginModelAPI
import com.wasterec.app.services.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserRepository : ApiService() {
    //Function to send credential to login endpoint
    fun login(loginBody : LoginModelAPI) {
        var loginAPIInterface = retrofit?.create(UserService::class.java)
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