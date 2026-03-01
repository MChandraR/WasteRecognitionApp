package com.wasterec.app.feature.importimage.viewmodel_factory

import android.app.Application
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wasterec.app.feature.importimage.viewmodel.ImportImageViewModel
import com.wasterec.app.model.TrainingModel

class ImportImageViewModelFactory(
    val application: Application,
    val trainingData : SnapshotStateList<TrainingModel>
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ImportImageViewModel::class.java)){
            return ImportImageViewModel(application = application, trainingData ) as T
        }

        throw IllegalArgumentException("Incorrect class type for ImportImageViewModel")
    }
}