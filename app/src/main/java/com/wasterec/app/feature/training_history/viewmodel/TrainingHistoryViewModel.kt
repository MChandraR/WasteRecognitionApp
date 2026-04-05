package com.wasterec.app.feature.training_history.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.AndroidViewModel
import androidx.navigation.NavHostController
import com.wasterec.app.model.api_response.training.TrainingDataResponse
import com.wasterec.app.model.domain.TrainingData
import com.wasterec.app.repositories.TrainingRepository

class TrainingHistoryViewModel(val app: Application, val navHostController: NavHostController, val selectedTrainingData : MutableState<TrainingData>?): AndroidViewModel(app) {
    val trainingHistoryRepository = TrainingRepository(app.baseContext)
    val trainingHistoryData : SnapshotStateList<TrainingData> = mutableStateListOf()

    fun fetchTrainingData(){
        trainingHistoryRepository.getTrainingDataHistoryForClient(
            onSuccess = { trainingDataList ->
                println("Berhasil mendapatkan data histroy ${trainingDataList.size}")
                trainingHistoryData.clear()
                trainingHistoryData.addAll(trainingDataList)
            },
            onFailed = {
                Toast.makeText(app.baseContext, it, Toast.LENGTH_SHORT).show()
            }
        )
    }
}