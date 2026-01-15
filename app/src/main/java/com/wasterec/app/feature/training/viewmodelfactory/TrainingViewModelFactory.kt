package com.wasterec.app.feature.training.viewmodelfactory

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.wasterec.app.feature.importimage.viewmodel.ImportImageViewModel
import com.wasterec.app.feature.training.viewmodel.TrainingViewModel

class TrainingViewModelFactory(
    val application: Application,
    val context: Context,
    val navHostController: NavHostController,
    val importImageViewModel: ImportImageViewModel
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(TrainingViewModel::class.java)){
            return TrainingViewModel(application, context, navHostController, importImageViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")    }
}