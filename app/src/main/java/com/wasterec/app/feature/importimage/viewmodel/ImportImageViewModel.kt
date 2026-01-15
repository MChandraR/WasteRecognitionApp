package com.wasterec.app.feature.importimage.viewmodel

import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.wasterec.app.model.TrainingModel
import kotlin.math.max
import kotlin.math.min

class ImportImageViewModel : ViewModel() {
    var imageDatasetList  = mutableStateListOf<TrainingModel>()

    fun getImageDataBitmap(index : Int) : Bitmap?{
        if(index >= 0 && index < imageDatasetList.size){
            return imageDatasetList[index].Input
        }
        return null
    }

    fun setLabelForImage(dataIndex : Int, labelIndex : Int){
        if(dataIndex >= 0 && dataIndex < imageDatasetList.size){
            imageDatasetList[dataIndex].Label = labelIndex
        }
    }
}