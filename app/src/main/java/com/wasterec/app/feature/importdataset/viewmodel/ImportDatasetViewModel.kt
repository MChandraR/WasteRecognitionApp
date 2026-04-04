package com.wasterec.app.feature.importdataset.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.AndroidViewModel
import androidx.navigation.NavHostController
import com.wasterec.app.feature.importdataset.data.DatasetClass
import com.wasterec.app.manager.DatasetManager
import com.wasterec.app.model.TrainingModel

class ImportDatasetViewModel(
    application : Application,
    val context: Context,
    val navHostController: NavHostController,
    val trainignDataset : SnapshotStateList<TrainingModel>,
    val selectedIndex : MutableState<Int>,
    val selectedLabel : MutableState<DatasetClass>,
    val datasetClassList : SnapshotStateList<DatasetClass>
) : AndroidViewModel(application = application) {

    val datasetManager = DatasetManager(trainignDataset)
    var classCount : MutableList<Int> = mutableStateListOf(0,0,0,0,0,0)
    val isClassCountMeetRequirement : MutableState<Boolean> = mutableStateOf(false)

    fun getTrainingDatasetCount():Int{
        return trainignDataset.size
    }

    fun getClassCount(){
        datasetManager.getEachLabelCount().forEachIndexed { idx, value ->
            classCount[idx] = value
        }
    }

    fun validateClassCount(){
        var isMeetMinMaxCount = true
        datasetManager.getEachLabelCount().forEachIndexed { idx, value ->
            println("Label ${datasetClassList[idx].className} : $value , minium : ${datasetClassList[idx].minimunCount}")
            if(value < datasetClassList[idx].minimunCount ){
                isMeetMinMaxCount = false
            }else if(value > datasetClassList[idx].maximumCount){
                isMeetMinMaxCount = false
            }
        }
        isClassCountMeetRequirement.value = isMeetMinMaxCount
    }

    fun resetState(){
        isClassCountMeetRequirement.value = false
    }

}