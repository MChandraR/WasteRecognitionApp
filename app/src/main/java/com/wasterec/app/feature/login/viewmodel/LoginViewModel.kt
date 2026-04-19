package com.wasterec.app.feature.login.viewmodel

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.lifecycle.AndroidViewModel
import androidx.navigation.NavHostController
import com.wasterec.app.model.Destination
import com.wasterec.app.model.api_request.LoginModelRequestAPI
import com.wasterec.app.repositories.UserRepository
import com.wasterec.app.services.SharedPreferenceService

class LoginViewModel(application : Application, val context: Context,
                     val navController: NavHostController
) : AndroidViewModel(application) {
    var username : MutableState<String> = mutableStateOf("")
    var password : MutableState<String> = mutableStateOf("")
    var showAlert : MutableState<Boolean> = mutableStateOf(false)
    val alertMessage : MutableState<String> = mutableStateOf("")
    var loginAPIService = UserRepository()
    var sharedPreferenceService : SharedPreferenceService = SharedPreferenceService(application.baseContext)
    var isSuccess : MutableState<Boolean> = mutableStateOf(false)
    var showPassword : MutableState<Boolean> = mutableStateOf(false)
    var focusManager : FocusManager? = null

    fun setLocalFoccues(focusManager : FocusManager){
        this.focusManager = focusManager
    }

    fun changeFocusToNextElement(){
        focusManager?.moveFocus(FocusDirection.Down)
    }

    fun changeFocusToDone(){
        focusManager?.clearFocus()
    }

    //Function to handle login and validate user input
    fun login(){
        if(username.value.isNotEmpty() && password.value.isNotEmpty()){
            try{
                loginAPIService.login(LoginModelRequestAPI(username = username.value, password = password.value),
                    onSuccess = {
                        sharedPreferenceService.storeStringValue("authToken", it.data.authToken)
                        alertMessage.value = it.message
                        isSuccess.value = true
                        showAlert.value = true
                    },
                    onFailed = {
                        alertMessage.value = it
                        isSuccess.value = false
                        showAlert.value = true
                    }
                )
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
        navigateToHome()
    }

    fun navigateToHome(){
        navController.navigate(Destination.Home) {
            popUpTo(navController.graph.startDestinationId) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }
}