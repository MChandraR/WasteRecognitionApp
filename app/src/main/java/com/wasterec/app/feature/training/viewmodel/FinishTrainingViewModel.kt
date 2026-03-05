package com.wasterec.app.feature.training.viewmodel

import android.app.Application
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.AndroidViewModel
import androidx.navigation.NavHostController
import com.wasterec.app.manager.DatasetManager
import com.wasterec.app.model.Destination
import com.wasterec.app.model.TrainingModel

class FinishTrainingViewModel(
    application: Application,
    val navHostController: NavHostController,
    val trainingData : SnapshotStateList<TrainingModel>,
    val datasetManager: DatasetManager
): AndroidViewModel(application = application){

    fun navigateBackToHome(){
        clearAllData()
        navHostController.navigate(Destination.Home) {
            popUpTo(navHostController.graph.startDestinationId) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    fun clearAllData(){
        trainingData.clear()
        datasetManager.clearAlLData()
    }
}