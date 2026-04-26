package com.wasterec.app.feature.classify.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.navigation.NavHostController
import com.wasterec.app.feature.importdataset.data.datasetClassList
import com.wasterec.app.manager.EfficientNetB0
import com.wasterec.app.model.Destination

class ClassifyViewModel(val app : Application, val navHostController : NavHostController) : AndroidViewModel(application = app){

    val currentBitMap: MutableState<Bitmap?> = mutableStateOf(null)
    val predictedResult = mutableStateOf("")
    val confidentLevel = mutableFloatStateOf(0f)
    val isOnInference = mutableStateOf(false)
    val counter = mutableIntStateOf(0)
    var efficientNetB0 : EfficientNetB0? = null

    fun init(){
        if(efficientNetB0 == null){
            efficientNetB0 = EfficientNetB0(app.baseContext, "Backbone.ptl")
        }
        currentBitMap.value = null
        predictedResult.value = "-"
        confidentLevel.floatValue = 0f
    }



    fun backToHome(){
        navHostController.navigate(Destination.Home){
            popUpTo(Destination.Home){
                inclusive = true
            }
            launchSingleTop = true
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun classifyImage(bitmap : Bitmap){
        isOnInference.value = true
        efficientNetB0?.backbonePredict(bitmap)?.let { output ->
            confidentLevel.floatValue = output.second
            println("Confident : ${output.second}")
            predictedResult.value = datasetClassList[output.first].className
            isOnInference.value = false
        }
    }
}