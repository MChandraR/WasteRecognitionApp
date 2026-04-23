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
import com.wasterec.app.utils.resizeWithEdgePadding
import com.wasterec.app.utils.rotateBitmap
import kotlin.math.ceil
import kotlin.random.Random

class DatasetManager (
    var trainingData : MutableList<TrainingModel>
){
    var lockTrainingDataFromPreprocessing = false
    val resizedCount = 0
    val rotatedCount = 0
    val horizontallyFlippedCount = 0
    val verticallyFlippedCount = 0
    var chancePreprocessPerLabel = arrayOf(5,5,5,5,5,5)

    fun loadData(trainingData : MutableList<TrainingModel>){
        this.trainingData.clear()
        this.trainingData = trainingData
        getEachLabelCount().forEachIndexed {  idx, value ->
            this.chancePreprocessPerLabel[idx] = ceil(value.toFloat() * 0.3f).toInt()
        }
    }

    fun clearAlLData(){
        this.trainingData = mutableListOf<TrainingModel>()
    }

    fun getData() : MutableList<TrainingModel>{
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
    fun preProcessTrainingData(resizeImage: Boolean = false, onProgress : (progress:Float)->Unit, onResult : ((dataType : List<Int> )->Unit)? = null): DatasetManager {
        if(lockTrainingDataFromPreprocessing) {
            onProgress(1f)
            return this
        }
        var resizedCount = 0
        var rotatedCount = 0
        var horizontallyFlippedCount = 0
        var verticallyFlippedCount = 0
        var totalDataCount = 0
        var ogTrainingData = mutableListOf<TrainingModel>()

        val processedTrainingData = trainingData.shuffled().map { item ->
            totalDataCount+=1
            onProgress(((totalDataCount/trainingData.size).toFloat()))
            var imageData = forceSoftwareBitmap(item.Input)
            var currentTypes = item.Type

            if (resizeImage ) {
                if (!(imageData.width == 224 && imageData.height == 224))imageData = resizeWithEdgePadding(imageData)
                currentTypes += DataTypeModel.RESIZED
                resizedCount++
            }

           if(chancePreprocessPerLabel[item.Label] > 0){
               chancePreprocessPerLabel[item.Label] -= 1
               val preprocessType = Random.nextInt(0,3)
               if(preprocessType == 0){
                   val (rotatedImg, isRotated) = applyRandomRotation(imageData, chance = 1.0)

                   ogTrainingData.add( TrainingModel(imageData, item.Label, currentTypes) )
                   imageData = rotatedImg
                   currentTypes += DataTypeModel.ROTATED
                   rotatedCount++

               }

               if(preprocessType == 1){
                   val (hFlippedImg, isHFlipped) = applyRandomHorizontalFlip(imageData, 1.0)
                   ogTrainingData.add( TrainingModel(imageData, item.Label, currentTypes) )
                   imageData = hFlippedImg
                   currentTypes += DataTypeModel.FLIPPED_HORIZONTALLY
                   horizontallyFlippedCount++
               }

               if(preprocessType == 2){
                   val (vFlippedImg, isVFlipped) = applyRandomVerticallyFlip(imageData,1.0)
                   ogTrainingData.add( TrainingModel(imageData, item.Label, currentTypes) )
                   imageData = vFlippedImg
                   currentTypes += DataTypeModel.FLIPPED_VERTICALLY
                   verticallyFlippedCount++
               }
           }


            TrainingModel(imageData, item.Label, currentTypes)
        }

        println("Total proses : ${processedTrainingData.size}")
        println("Total og : ${ogTrainingData.size}")

        this.trainingData = processedTrainingData.toMutableList()
        this.trainingData.addAll(ogTrainingData)
        onResult?.invoke(listOf(resizedCount, rotatedCount, horizontallyFlippedCount, verticallyFlippedCount))

        println("Image count : ${trainingData.size}")
        println("Resized Image : $resizedCount")
        println("Rotated Image : $rotatedCount")
        println("Horizontally Flipped Image : $horizontallyFlippedCount")
        println("Vertically Flipped Image : $verticallyFlippedCount")

        rotatedCount = 0
        resizedCount = 0
        horizontallyFlippedCount = 0
        verticallyFlippedCount = 0

        return this
    }

    fun applyRandomRotation(bitmap : Bitmap, chance : Double = .1): Pair<Bitmap, Boolean> {
        val angles = listOf(30f,45f,60f )
        if(generateBooleanWithChance(chance)){
            return Pair(rotateBitmap(bitmap, angles.random()), true)
        }
        return Pair(bitmap,false)
    }

    fun applyRandomHorizontalFlip(bitmap: Bitmap, chance:Double = 0.1): Pair<Bitmap, Boolean>{
        if(generateBooleanWithChance(chance)){
            return Pair(flipHorizontal(bitmap), true)
        }
        return Pair(bitmap, false)
    }

    fun applyRandomVerticallyFlip(bitmap: Bitmap, chance:Double = 0.1): Pair<Bitmap, Boolean>{
        if(generateBooleanWithChance(chance)){
            return Pair(flipVertical(bitmap), true)
        }
        return Pair(bitmap, false)
    }

}