package com.wasterec.app.feature.importimage.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import com.wasterec.app.feature.importdataset.data.DatasetClass
import com.wasterec.app.feature.importdataset.data.datasetClassList
import com.wasterec.app.model.TrainingModel
import com.wasterec.app.utils.resizeAndCropCenter


class ImportImageViewModel(
    application: Application ,
    var imageDatasetList : SnapshotStateList<TrainingModel>,
    val selectedLabelIndex : MutableState<Int>,
    val selectedLabel : MutableState<DatasetClass>
) : AndroidViewModel(application = application) {

    var showConfirmImageDeletionDialog : MutableState<Boolean> = mutableStateOf(false)
    val selectedImageIndex : MutableState<Int> = mutableIntStateOf(0)
    val label = arrayOf("Plastik", "Kertas", "Kaca",  "Logam", "Kardus", "Sampah")
    val importedImageList : SnapshotStateList<Bitmap> = SnapshotStateList()

    fun reInit(){
        importedImageList.clear()
        getDatasetForSelectedLabel().forEach {
            importedImageList.add(it.Input)
        }
//        imageDatasetList.clear()
//        showConfirmImageDeletionDialog.value = false
//        selectedImageIndex.value = 0
    }

    fun addDataToImportedImageList(bitmap: Bitmap){
        println("Menambahkan data")
        importedImageList.add(bitmap)
    }

    fun saveImportedImageListToDataset(){
        imageDatasetList.removeAll({it.Label == selectedLabelIndex.value})
        importedImageList.forEach {
            increaseItemCountForSelectedLabelinDataset()
            imageDatasetList.add(
                TrainingModel(
                    resizeAndCropCenter(it),
                    selectedLabelIndex.value
                )
            )
        }
    }

    fun removeDataFromImportedImageList(index : Int){
        importedImageList.removeAt(index)
    }

    fun getImageDataBitmap(index : Int) : Bitmap?{
        if(index >= 0 && index < imageDatasetList.size){
            return imageDatasetList[index].Input
        }
        return null
    }

    fun getEachLabelCount(): IntArray{
        val labelOfLabelCount = IntArray(6)

        imageDatasetList.forEach {
            labelOfLabelCount.set(it.Label, labelOfLabelCount.get(it.Label)+1)
        }
        return labelOfLabelCount
    }

    fun validateDataForCurrentLabelBeforeInput(action : ()->Unit){
        var index : Int = label.indexOf(selectedLabel.value.className)
        if(getEachLabelCount().get(index) <= datasetClassList.get(index).maximumCount){
            action()
        }else{
            Toast.makeText(application.baseContext, "Maxsimum data tercapai", Toast.LENGTH_LONG).show()
        }
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

    fun getDatasetForSelectedLabel():List<TrainingModel>{
        return imageDatasetList.filter { it.Label == selectedLabelIndex.value }
    }

    fun getTotalOfDatasetForSelectedLabel():Int{
        return imageDatasetList.filter { it.Label == selectedLabelIndex.value }.size
    }

    fun getSelectedLabel():String{
        return label.get(selectedLabelIndex.value)
    }

    fun increaseItemCountForSelectedLabelinDataset(){
        datasetClassList[selectedLabelIndex.value].currentCount += 1
    }

    fun decreaseItemCountForSelectedLabelinDataset(){
        datasetClassList[selectedLabelIndex.value].currentCount -= 1
    }
}