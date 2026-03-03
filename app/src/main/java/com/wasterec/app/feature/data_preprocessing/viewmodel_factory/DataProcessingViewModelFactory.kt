package com.wasterec.app.feature.data_preprocessing.viewmodel_factory

import android.app.Application
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.wasterec.app.feature.data_preprocessing.viewmodel.DataProcessingViewModel
import com.wasterec.app.manager.DatasetManager
import com.wasterec.app.model.TrainingModel

class DataProcessingViewModelFactory(
    val application: Application,
    val navHostController: NavHostController,
    val trainingData : SnapshotStateList<TrainingModel>,
    val datasetManager: DatasetManager
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(DataProcessingViewModel::class.java)){
            return DataProcessingViewModel(application, navHostController, trainingData, datasetManager) as T
        }
        throw IllegalArgumentException("Incorrect viewmodel class for DataProcessingViewModel")
    }
}