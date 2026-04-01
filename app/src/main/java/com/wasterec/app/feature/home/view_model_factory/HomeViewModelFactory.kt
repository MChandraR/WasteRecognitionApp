package com.wasterec.app.feature.home.view_model_factory

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.wasterec.app.feature.home.viewmodel.HomeViewModel

class HomeViewModelFactory(
    val application: Application,
    val navHostController: NavHostController,
    val context: Context,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(HomeViewModel::class.java)){
            return HomeViewModel( navHostController,application) as T
        }
        throw IllegalArgumentException("Wrong viewmodel class for homw view model")
    }
}