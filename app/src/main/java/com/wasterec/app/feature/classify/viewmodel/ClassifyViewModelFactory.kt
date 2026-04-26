package com.wasterec.app.feature.classify.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController

class ClassifyViewModelFactory(
    val application: Application,
    val navHostController: NavHostController
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ClassifyViewModel::class.java)){
            return ClassifyViewModel(application, navHostController) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class for Classify View Model")

    }
}