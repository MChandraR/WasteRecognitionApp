package com.wasterec.app.feature.training_history.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.AndroidViewModel
import androidx.navigation.NavHostController
import com.wasterec.app.helper.ExpiredAuthTokenException
import com.wasterec.app.model.Destination
import com.wasterec.app.model.api_response.training.TrainingDataResponse
import com.wasterec.app.model.domain.TrainingData
import com.wasterec.app.repositories.TrainingRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TrainingHistoryViewModel(val app: Application, val navHostController: NavHostController, val selectedTrainingData : MutableState<TrainingData>?): AndroidViewModel(app) {
    val trainingHistoryRepository = TrainingRepository(app.baseContext, {handleAPIException(it)})
    val trainingHistoryData : SnapshotStateList<TrainingData> = mutableStateListOf()

    fun fetchTrainingData(){
        trainingHistoryRepository.getTrainingDataHistoryForClient(
            onSuccess = { trainingDataList ->
                trainingHistoryData.clear()
                trainingHistoryData.addAll(trainingDataList)
            },
            onFailed = {

            }
        )
    }

    fun handleAPIException(e : Exception){
        when(e){
            is ExpiredAuthTokenException -> {
                navigateToHome()
            }
            else -> {

            }
        }
    }

    fun navigateToHome(){
        if(navHostController.currentBackStackEntry?.destination?.route == "com.wasterec.app.model.Destination.Home"){
            Toast.makeText(app.baseContext, "Sesi sudah expire silahkan login ulang !", Toast.LENGTH_SHORT).show()
            CoroutineScope(Dispatchers.Main).launch {
                navHostController.navigate(Destination.Login){
                    popUpTo(Destination.Login) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        }

    }
}