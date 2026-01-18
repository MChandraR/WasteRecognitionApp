package com.wasterec.app.feature.importimage.viewmodel

import android.graphics.Bitmap
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.wasterec.app.model.TrainingModel
import kotlin.math.max
import kotlin.math.min

class ImportImageViewModel : ViewModel() {
    var imageDatasetList  = mutableStateListOf<TrainingModel>()
    var showConfirmImageDeletionDialog : MutableState<Boolean> = mutableStateOf(false)
    val selectedImageIndex : MutableState<Int> = mutableIntStateOf(0)

    fun reInit(){
        imageDatasetList = mutableStateListOf()
        showConfirmImageDeletionDialog.value = false
        selectedImageIndex.value = 0
    }

    fun getImageDataBitmap(index : Int) : Bitmap?{
        if(index >= 0 && index < imageDatasetList.size){
            return imageDatasetList[index].Input
        }
        return null
    }

    fun getEachLabelCount(): IntArray{
        val labelOfLabelCount : IntArray = IntArray(6)

        imageDatasetList.forEach {
            labelOfLabelCount.set(it.Label, labelOfLabelCount.get(it.Label)+1)
        }
        return labelOfLabelCount
    }

    fun deleteDataFromDataset(index : Int){
        if(index >= 0 && index < imageDatasetList.size){
             imageDatasetList.removeAt(index)
        }
    }

    fun setLabelForImage(dataIndex : Int, labelIndex : Int){
        if(dataIndex >= 0 && dataIndex < imageDatasetList.size){
            imageDatasetList[dataIndex].Label = labelIndex
        }
    }
}