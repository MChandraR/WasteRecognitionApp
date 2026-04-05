package com.wasterec.app.feature.training_history.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TrainingHistoryViewModelFactory(
    val application : Application,
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(TrainingHistoryViewModel::class.java)){
            return TrainingHistoryViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class for TrainingHistory Viewmodel")

    }
}