package com.wasterec.app.feature.importdataset.viewmodel_factory

import android.app.Application
import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.wasterec.app.feature.importdataset.data.DatasetClass
import com.wasterec.app.feature.importdataset.viewmodel.ImportDatasetViewModel
import com.wasterec.app.model.TrainingModel

class ImportDatasetViewModelFactory(
    val application: Application,
    val context: Context,
    val navHostController: NavHostController,
    val trainingDataset : SnapshotStateList<TrainingModel>,
    val selectedIndex : MutableState<Int>,
    val selectedLabel : MutableState<DatasetClass>,
    val datasetClassList : SnapshotStateList<DatasetClass>
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ImportDatasetViewModel::class.java)){
            return ImportDatasetViewModel(application = application, context = context, navHostController = navHostController,trainignDataset = trainingDataset, selectedIndex, selectedLabel,datasetClassList) as T
        }
        throw IllegalArgumentException("Incorrect view model class for importdataset view model")
    }
}