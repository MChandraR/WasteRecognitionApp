package com.wasterec.app.presentation.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import com.wasterec.app.model.LoginModelAPI
import com.wasterec.app.services.LoginAPIService
import com.wasterec.app.services.SharedPreferenceService

class LoginViewModel(var context : Context) {
    var username : MutableState<TextFieldValue> = mutableStateOf(TextFieldValue(""))
    var password : MutableState<TextFieldValue> = mutableStateOf(TextFieldValue(""))
    var loginAPIService = LoginAPIService()
    var sharedPreferenceService : SharedPreferenceService = SharedPreferenceService(context)

    fun login(){
        if(username.value.text.isNotEmpty() && password.value.text.isNotEmpty()){
            try{
                loginAPIService.login(LoginModelAPI(username = username.value.text, password = password.value.text))
            }catch(e : Exception){
                println("Error : ${e.message}")
            }
            return
        }

        Toast.makeText(this.context, "Isi username dan password dengan benar" , Toast.LENGTH_SHORT).show()
    }
}