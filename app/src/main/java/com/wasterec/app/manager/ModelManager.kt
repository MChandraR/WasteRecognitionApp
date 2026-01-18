package com.wasterec.app.manager

import android.content.Context
import android.util.Log
import com.wasterec.app.utils.IOUtils
import org.json.JSONObject
import org.pytorch.LiteModuleLoader
import org.pytorch.Module
import kotlin.math.exp

open class ModelManager(private val context : Context, private val modelPath : String) {
    private var model : Module? = null
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
            model = LiteModuleLoader.load(modelPath)

            val (w, b) = loadClassifierParams(context)
            classifierWeights = w
            classifierBias = b

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

    fun loadClassifierParams(context: Context): Pair<Array<FloatArray>, FloatArray> {
        val jsonStr = context.assets.open("param.json")
            .bufferedReader().use { it.readText() }

        val json = JSONObject(jsonStr)

        // Ambil array bobot (2D)
        val weightArray = json.getJSONArray("weight")
        val weights = Array(weightArray.length()) { i ->
            val row = weightArray.getJSONArray(i)
            FloatArray(row.length()) { j -> row.getDouble(j).toFloat() }
        }

        // Ambil array bias (1D)
        val biasArray = json.getJSONArray("bias")
        val bias = FloatArray(biasArray.length()) { i -> biasArray.getDouble(i).toFloat() }

        Log.d("ModelParam", "Loaded weights shape: [${weights.size}, ${weights[0].size}]")
        Log.d("ModelParam", "Loaded bias shape: [${bias.size}]")

        return weights to bias
    }


}