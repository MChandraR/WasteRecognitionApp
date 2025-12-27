package com.wasterec.app.feature.login.factory

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.wasterec.app.feature.login.viewmodel.LoginViewModel

class LoginViewModelFactory(
    var app:Application,
    var context : Context,
    var navHostController: NavHostController
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(app,context, navController = navHostController) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}