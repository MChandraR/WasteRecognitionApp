package com.wasterec.app.feature.neural_search

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class NSViewModelFactory(val app : Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(NSViewModel::class.java)){
            return NSViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}