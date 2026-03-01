package com.wasterec.app.feature.modelload.factory

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.wasterec.app.feature.modelload.viewmodel.ModelLoadViewModel
import com.wasterec.app.feature.training.viewmodel.TrainingViewModel

class ModelLoadViewModelFactory(
    val application: Application,
    val context: Context,
    val navHostController : NavHostController
): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ModelLoadViewModel::class.java)){
            return ModelLoadViewModel(application,context, navHostController) as T
        }
        throw IllegalArgumentException("Invalid ViewModel Type")
    }
}