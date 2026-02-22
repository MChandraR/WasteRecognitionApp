package com.wasterec.app.feature.anotate.factory

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.wasterec.app.feature.anotate.viewmodel.AnotateViewModel

class AnotateViewModelFactory(
    val application: Application,
    val context : Context,
    val navHostController: NavHostController
):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AnotateViewModel::class.java)) {
            return AnotateViewModel(application = application, context, navHostController) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}