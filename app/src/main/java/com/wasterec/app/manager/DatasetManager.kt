package com.wasterec.app.manager

import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import com.wasterec.app.model.DataTypeModel
import com.wasterec.app.model.TrainingModel
import com.wasterec.app.utils.flipHorizontal
import com.wasterec.app.utils.flipVertical
import com.wasterec.app.utils.forceSoftwareBitmap
import com.wasterec.app.utils.generateBooleanWithChance
import com.wasterec.app.utils.resizeAndCropCenter
import com.wasterec.app.utils.rotateBitmap

class DatasetManager (
    var trainingData : List<TrainingModel>
){
    fun loadData(trainingData : List<TrainingModel>){
        this.trainingData = trainingData
    }

    fun clearAlLData(){
        this.trainingData = listOf<TrainingModel>()
    }

    fun getData() : List<TrainingModel>{
        return this.trainingData
    }

    fun setLabelForImage(dataIndex : Int, labelIndex : Int){
        if(dataIndex >= 0 && dataIndex < trainingData.size){
            trainingData[dataIndex].Label = labelIndex
        }
    }
    fun getDataSize():Int{
        return this.trainingData.size
    }

    fun getEachLabelCount(): IntArray{
        val labelOfLabelCount = IntArray(6)

        trainingData.forEach {
            labelOfLabelCount.set(it.Label, labelOfLabelCount.get(it.Label)+1)
        }
        return labelOfLabelCount
    }

    fun getImageDataBitmap(index : Int) : Bitmap?{
        if(index >= 0 && index < trainingData.size){
            return trainingData[index].Input
        }
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun preProcessTrainingData(resizeImage: Boolean = false): DatasetManager {
        var resizedCount = 0
        var rotatedCount = 0
        var horizontallyFlippedCount = 0
        var verticallyFlippedCount = 0

        val processedTrainingData = trainingData.map { item ->
            var imageData = forceSoftwareBitmap(item.Input)
            var currentTypes = item.Type

            if (resizeImage) {
                imageData = resizeAndCropCenter(imageData)
                currentTypes += DataTypeModel.RESIZED
                resizedCount++
            }

            val (rotatedImg, isRotated) = applyRandomRotation(imageData)

            if (isRotated) {
                imageData = rotatedImg
                currentTypes += DataTypeModel.ROTATED
                rotatedCount++
            } else {
                val (hFlippedImg, isHFlipped) = applyRandomHorizontalFlip(imageData)
                if (isHFlipped) {
                    imageData = hFlippedImg
                    currentTypes += DataTypeModel.FLIPPED_HORIZONTALLY
                    horizontallyFlippedCount++
                } else {
                    val (vFlippedImg, isVFlipped) = applyRandomVerticallyFlip(imageData)
                    if (isVFlipped) {
                        imageData = vFlippedImg
                        currentTypes += DataTypeModel.FLIPPED_VERTICALLY
                        verticallyFlippedCount++
                    }
                }
            }

            TrainingModel(imageData, item.Label, currentTypes)
        }

        this.trainingData = processedTrainingData

        println("Image count : ${trainingData.size}")
        println("Resized Image : $resizedCount")
        println("Rotated Image : $rotatedCount")
        println("Horizontally Flipped Image : $horizontallyFlippedCount")
        println("Vertically Flipped Image : $verticallyFlippedCount")

        return this
    }

    fun applyRandomRotation(bitmap : Bitmap, chance : Double = .9): Pair<Bitmap, Boolean> {
        val angles = listOf(30f,45f,50f )
        if(generateBooleanWithChance(chance)){
            return Pair(rotateBitmap(bitmap, angles.random()), true)
        }
        return Pair(bitmap,false)
    }

    fun applyRandomHorizontalFlip(bitmap: Bitmap, chance:Double = 0.9): Pair<Bitmap, Boolean>{
        if(generateBooleanWithChance(chance)){
            return Pair(flipHorizontal(bitmap), true)
        }
        return Pair(bitmap, false)
    }

    fun applyRandomVerticallyFlip(bitmap: Bitmap, chance:Double = 0.9): Pair<Bitmap, Boolean>{
        if(generateBooleanWithChance(chance)){
            return Pair(flipVertical(bitmap), true)
        }
        return Pair(bitmap, false)
    }

}