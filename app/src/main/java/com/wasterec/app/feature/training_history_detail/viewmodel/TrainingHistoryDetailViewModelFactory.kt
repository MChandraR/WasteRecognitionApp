package com.wasterec.app.feature.training_history_detail.viewmodel

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.wasterec.app.model.domain.TrainingData

class TrainingHistoryDetailViewModelFactory (
    val application: Application,
    val navHostController: NavHostController,
    val selectedTrainingHistory : MutableState<TrainingData>?
): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(TrainingHistoryDetailViewModel::class.java)){
            return TrainingHistoryDetailViewModel(application, navHostController, selectedTrainingHistory) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class for TrainingHistory Detail Viewmodel")

    }
}