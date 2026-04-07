package com.wasterec.app.manager

import android.content.Context
import com.wasterec.app.utils.IOUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.pytorch.executorch.Module
import kotlin.math.exp

open class ModelManager(private val context : Context, private val modelPath : String) {
    private var model : Module? = null
    private var classifierWeightFileManager = ClassifierWeightFileManager(context)
    var classifierWeights: Array<FloatArray>? = null
        private set
    var classifierBias: FloatArray? = null
        private set

    fun setClassifierWeight(weight : Array<FloatArray>){
        this.classifierWeights = weight
    }

    fun setClassifierBias(bias : FloatArray){
        this.classifierBias = bias
    }
    fun loadModel(): Module {
        val modelPath = IOUtils.assetFilePath(context, modelPath)
        //println("Lokasi model: $modelPath")

        if (model == null) {
            model = Module.load(modelPath)

            loadClassifierParams(){
                classifierWeights = it.first
                classifierBias = it.second
            }

            //println("✅ Model loaded, weights = [${w.size} x ${w[0].size}], bias = [${b.size}]")
        }

        return model!!
    }


    fun softmax(logits: FloatArray): FloatArray {
        val maxLogit = logits.maxOrNull() ?: 0f
        val expValues = logits.map { exp(it - maxLogit) }
        val sumExp = expValues.sum()
        return expValues.map { it / sumExp }.toFloatArray()
    }

    fun loadClassifierParams( result : (Pair<Array<FloatArray>, FloatArray>) -> Unit ) {
        CoroutineScope(Dispatchers.IO).launch{
            val classifierParam = classifierWeightFileManager.loadClassifierParamFromFile()
            if(classifierParam != null){
                result(classifierParam.weights to classifierParam.bias)
            }
        }
    }


}