package com.wasterec.app.feature.importdataset.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.AndroidViewModel
import androidx.navigation.NavHostController
import com.wasterec.app.feature.importdataset.data.DatasetClass
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


    fun getTrainingDatasetCount():Int{
        return trainignDataset.size
    }


}