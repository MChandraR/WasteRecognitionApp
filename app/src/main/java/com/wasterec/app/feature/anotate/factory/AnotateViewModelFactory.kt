package com.wasterec.app.feature.anotate.factory

import android.app.Application
import android.content.Context
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.wasterec.app.feature.anotate.viewmodel.AnnotateViewModel
import com.wasterec.app.model.TrainingModel

class AnotateViewModelFactory(
    val application: Application,
    val context : Context,
    val navHostController: NavHostController,
    val trainingData : SnapshotStateList<TrainingModel>
):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AnnotateViewModel::class.java)) {
            return AnnotateViewModel(application = application, context, navHostController, trainingData) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}