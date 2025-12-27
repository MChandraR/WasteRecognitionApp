package com.wasterec.app.feature.login.viewmodel

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.AndroidViewModel
import androidx.navigation.NavHostController
import com.wasterec.app.model.Destination
import com.wasterec.app.model.LoginModelAPI
import com.wasterec.app.repositories.UserRepository
import com.wasterec.app.services.SharedPreferenceService

class LoginViewModel(application : Application, val context: Context, navController : NavHostController) : AndroidViewModel(application) {
    var username : MutableState<TextFieldValue> = mutableStateOf(TextFieldValue(""))
    var password : MutableState<TextFieldValue> = mutableStateOf(TextFieldValue(""))
    var showAlert : MutableState<Boolean> = mutableStateOf(false)
    var loginAPIService = UserRepository()
    var sharedPreferenceService : SharedPreferenceService = SharedPreferenceService(context)
    val navController : NavHostController = navController

    //Function to handle login and validate user input
    fun login(){
        if(username.value.text.isNotEmpty() && password.value.text.isNotEmpty()){
            try{
                loginAPIService.login(LoginModelAPI(username = username.value.text, password = password.value.text))
                showAlert.value = true
            }catch(e : Exception){
                println("Error : ${e.message}")
            }
            return
        }
        Toast.makeText(this.context, "Isi username dan password dengan benar" , Toast.LENGTH_SHORT).show()
    }

    //Function for redirect user to next page after succesfully logged=in
    fun dismissAlert(){
        this.showAlert.value = false
        this.navController.navigate(Destination.Home)
    }
}