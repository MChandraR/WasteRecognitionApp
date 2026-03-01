package com.wasterec.app.feature.importdataset.viewmodel_factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.wasterec.app.feature.importdataset.viewmodel.ImportDatasetViewModel

class ImportDatasetViewModelFactory(
    val application: Application,
    val navHostController: NavHostController
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ImportDatasetViewModel::class.java)){
            return ImportDatasetViewModel(application = application, navHostController = navHostController) as T
        }
        throw IllegalArgumentException("Incorrect view model class for importdataset view model")
    }
}