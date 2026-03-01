package com.wasterec.app.feature.importdataset.viewmodel_factory

import android.app.Application
import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.wasterec.app.feature.importdataset.data.DatasetClass
import com.wasterec.app.feature.importdataset.viewmodel.ImportDatasetViewModel

class ImportDatasetViewModelFactory(
    val application: Application,
    val context: Context,
    val navHostController: NavHostController,
    val selectedIndex : MutableState<Int>,
    val selectedLabel : MutableState<DatasetClass>
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ImportDatasetViewModel::class.java)){
            return ImportDatasetViewModel(application = application, context = context, navHostController = navHostController, selectedIndex, selectedLabel) as T
        }
        throw IllegalArgumentException("Incorrect view model class for importdataset view model")
    }
}