package com.wasterec.app.feature.training.viewmodelfactory

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.wasterec.app.feature.training.viewmodel.FinishTrainingViewModel
import com.wasterec.app.manager.DatasetManager
import com.wasterec.app.model.TrainingModel

class FinishTrainingViewModelFactory(
    val application: Application,
    val navHostController: NavHostController,
    val trainingData : SnapshotStateList<TrainingModel>,
    val datasetManager: MutableState<DatasetManager>
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(FinishTrainingViewModel::class.java)){
            return FinishTrainingViewModel(application, navHostController, trainingData, datasetManager) as T
        }
        throw IllegalArgumentException("Incorrect class for FinishTrainingViewModel")
    }
}