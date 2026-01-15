package com.wasterec.app.feature.loading.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.wasterec.app.feature.loading.viewmodel.LoadGlobalWeightLoadingViewModel

class LoadGlobalWeightLoadingViewModelFactory(
    val navHostController: NavHostController,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoadGlobalWeightLoadingViewModel(navHostController = navHostController) as T
    }
}