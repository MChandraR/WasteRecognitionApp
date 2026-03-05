package com.wasterec.app.feature.data_preprocessing.viewmodel

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.AndroidViewModel
import androidx.navigation.NavHostController
import com.wasterec.app.manager.DatasetManager
import com.wasterec.app.model.Destination
import com.wasterec.app.model.TrainingModel

class DataProcessingViewModel(
    application: Application,
    val navHostController: NavHostController,
    val trainingData : SnapshotStateList<TrainingModel>,
    val datasetManager: DatasetManager
    ) : AndroidViewModel(application=application) {

    @RequiresApi(Build.VERSION_CODES.O)
    fun proProcessData(){
        datasetManager.loadData(trainingData)
        datasetManager.preProcessTrainingData(resizeImage = false)
    }

    fun navigateToAnnotatePage(){
        trainingData.clear()
        trainingData.addAll(datasetManager.getData())
        navHostController.navigate(Destination.Annotate)
    }
}