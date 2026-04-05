package com.wasterec.app.feature.training_history.viewmodel

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.wasterec.app.model.domain.TrainingData

class TrainingHistoryViewModelFactory(
    val application : Application,
    val navHostController: NavHostController,
    val selectedTrainingData : MutableState<TrainingData>?
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(TrainingHistoryViewModel::class.java)){
            return TrainingHistoryViewModel(application, navHostController, selectedTrainingData) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class for TrainingHistory Viewmodel")

    }
}