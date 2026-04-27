package com.wasterec.app.feature.data_preprocessing.viewmodel

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
    val datasetManager: MutableState<DatasetManager>
    ) : AndroidViewModel(application=application) {


    val dataTypeCount : MutableList<Int> = mutableListOf(0,0,0,0)
    val resizedCount : MutableIntState = mutableIntStateOf(0)
    val rotatedCount : MutableIntState = mutableIntStateOf(0)
    val horizontallyFlippedCount : MutableIntState = mutableIntStateOf(0)
    val verticallyFlippedCount : MutableIntState = mutableIntStateOf(0)

    val dataProcessingProgress : MutableFloatState = mutableFloatStateOf(0f)

    fun reInit(){
        dataProcessingProgress.floatValue = 0f
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun proProcessData(){
        if (!datasetManager.value.lockTrainingDataFromPreprocessing) {
//            datasetManager.value.loadData(trainingData.map { it.copy() }.toMutableList())
            datasetManager.value.preProcessTrainingData(
                resizeImage = true,
                onProgress = { dataProcessingProgress.floatValue = it }) {
                resizedCount.intValue = it[0]
                rotatedCount.intValue = it[1]
                horizontallyFlippedCount.intValue = it[2]
                verticallyFlippedCount.intValue = it[3]
                println("Total rotated dll : ${it.sum()}")
            }
            datasetManager.value.lockTrainingDataFromPreprocessing = true
        }else{
            dataProcessingProgress.floatValue = 1f
        }
    }


    fun navigateToAnnotatePage(){
//        trainingData.clear()
//        trainingData.addAll(datasetManager.value.getData())
        navHostController.navigate(Destination.Annotate)
    }

    fun navigateToTrainingPage(){
        navHostController.navigate(Destination.Training)
    }
}