package com.wasterec.app.feature.loading.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.wasterec.app.model.Destination
import com.wasterec.app.repositories.GlobalModelRepository
import kotlinx.coroutines.launch

class LoadGlobalWeightLoadingViewModel(
    val navHostController: NavHostController,
    val globalModelRepository: GlobalModelRepository = GlobalModelRepository()
): ViewModel() {
    fun fetchRecentGlobalModel(){
        globalModelRepository.fetchGlobalModelWeight({ error, result ->
            viewModelScope.launch {
                if(error == null){
                    println("Berhasil memuat model global !")
                    navHostController.navigate(Destination.Annotate)
                }
            }

        })
    }
}