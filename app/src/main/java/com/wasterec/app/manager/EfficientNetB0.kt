package com.wasterec.app.manager

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.internal.`$Gson$Types`.arrayOf
import com.wasterec.app.model.ClassifierWeightModel
import com.wasterec.app.model.ModelConiguration
import com.wasterec.app.model.SlidingArray
import com.wasterec.app.model.TrainingModel
import com.wasterec.app.utils.bitmapToFloatArray
import com.wasterec.app.utils.bitmapToFloatBuffer
import com.wasterec.app.utils.forceSoftwareBitmap
import org.pytorch.executorch.EValue
import org.pytorch.executorch.Module
import org.pytorch.executorch.Tensor
import kotlin.math.abs
import kotlin.math.ln

class EfficientNetB0(val context: Context,  modelPath : String = "backbone.ptl") : ModelManager(context, modelPath) {
    val model: Module = this.loadModel()

    @RequiresApi(Build.VERSION_CODES.O)
    fun predict(bitmap: Bitmap): Pair<Int, Float> {
        val safeBitmap = forceSoftwareBitmap(bitmap)

        val floatData = bitmapToFloatArray(safeBitmap)

        val shape = longArrayOf(1, 3, 224, 224) // Sesuaikan input EfficientNet
        val inputTensor = Tensor.fromBlob(floatData, shape)

        val inputs : Array<out EValue?> = arrayOf(EValue.from(inputTensor))
        val outputs = model.forward(*inputs)

        val outputTensor = outputs[0].toTensor()
        val scores = outputTensor.getDataAsFloatArray()

        val maxLogit = scores.maxOrNull() ?: 0f
        val expScores = scores.map { kotlin.math.exp(it - maxLogit) }
        val sumExp = expScores.sum()
        val probabilities = expScores.map { it / sumExp }

        val outputIdx = probabilities.indices.maxByOrNull { probabilities[it] } ?: -1
        val confidence = if (outputIdx != -1) probabilities[outputIdx] else 0f

        return Pair(outputIdx, confidence)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun backbonePredict(bitmap: Bitmap): Pair<Int, Float> {
        val startTime = System.currentTimeMillis()

        // 1. Preprocessing (Bitmap to Buffer)
        val startPre = System.currentTimeMillis()
        val safeBitmap = forceSoftwareBitmap(bitmap)

        // Pastikan fungsi ini mengembalikan FloatBuffer/FloatArray yang sudah ternormalisasi
        val floatData = bitmapToFloatBuffer(safeBitmap)
        val endPre = System.currentTimeMillis()
        println("⏱️ Preprocessing Time: ${endPre - startPre}ms")

        // 2. Loading Params (Hati-hati: Jika loadClassifierParams async, ini bisa jadi masalah)
        val startLoad = System.currentTimeMillis()
        var classifierParam = ClassifierWeightModel(arrayOf(floatArrayOf()), floatArrayOf())
        loadClassifierParams {
            classifierParam = ClassifierWeightModel(it.first, it.second)
        }
        val endLoad = System.currentTimeMillis()
        println("⏱️ Load Params Time: ${endLoad - startLoad}ms")

        // 3. Inference Backbone (ExecuTorch)
        val startAray = System.currentTimeMillis()

        val shape = longArrayOf(1, 3, 224, 224)
        val inputTensor = Tensor.fromBlob(floatData, shape)
        val inputArray = arrayOf(EValue.from(inputTensor))
        val endAray = System.currentTimeMillis()

        println("⏱️ Tensor Time: ${endAray - startAray}ms")
        val startInference = System.currentTimeMillis()

        val output = model.forward(*inputArray)
        val outputTensor = output[0].toTensor()
        val features = outputTensor.dataAsFloatArray // Gunakan getDataAsFloatArray untuk ExecuTorch
        val endInference = System.currentTimeMillis()
        println("⏱️ Backbone Inference Time: ${endInference - startInference}ms")

        // 4. Manual Linear Layer (Matrix Multiplication)
        val startLinear = System.currentTimeMillis()
        val numClasses = classifierParam.bias.size
        val logits = FloatArray(numClasses) { i ->
            var logit: Float = classifierParam.bias[i]
            val currentWeights = classifierParam.weights[i]
            for (j in 0 until 1280) {
                logit += features[j] * currentWeights[j]
            }
            logit
        }
        val endLinear = System.currentTimeMillis()
        println("⏱️ Manual Linear Layer Time: ${endLinear - startLinear}ms")

        // 5. Softmax & Final Result
        val startSoftmax = System.currentTimeMillis()
        val maxLogits = logits.maxOrNull() ?: 0f
        val expLogits = logits.map { kotlin.math.exp(it - maxLogits) }
        val totalExpLogits = expLogits.sum()
        val probabilities = expLogits.map { it / totalExpLogits }

        if (features.any { it.isNaN() }) {
            println("ERROR: Model output contains NaN")
            return Pair(-1, 0f)
        }

        val outputIdx = probabilities.indices.maxByOrNull { probabilities[it] } ?: -1
        val confidence = if (outputIdx != -1) probabilities[outputIdx] else 0f
        val endSoftmax = System.currentTimeMillis()

        println("⏱️ Softmax Time: ${endSoftmax - startSoftmax}ms")
        println("🚀 TOTAL PREDICTION TIME: ${endSoftmax - startTime}ms")

        return Pair(outputIdx, confidence)
    }

    fun setClassifierWeightAndBias(newWeight : Array<FloatArray>, newBias : FloatArray){
        this.setClassifierBias(newBias)
        this.setClassifierWeight(newWeight)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun train(config: ModelConiguration, dataset: List<TrainingModel>, onProgressUpdate : (epoch:Int, loss : Float)->Unit, onFinished : (totalEpoch:Int)->Unit = {}): Map<String, Any> {
        var weights = this.classifierWeights
        var bias = this.classifierBias
        var last3Loss = SlidingArray<Float>(maxSize = 3)

        if (weights == null || bias == null) return emptyMap()
        var totalEpoch = config.epoch


        val smoothingValue = 0.1f
        val weightDecay = 0.01f
        val numClasses = weights.size
        val numFeatures = weights[0].size
        val learningRate = config.learningRate

        // 1. Ekstraksi fitur (Caching) - Backbone Frozen
        val featureList = dataset.map { data ->
            val safeBitmap = if (data.Input.config == Bitmap.Config.HARDWARE) {
                data.Input.copy(Bitmap.Config.ARGB_8888, false)
            } else { data.Input }

            val floatData = bitmapToFloatBuffer(safeBitmap)
            val shape = longArrayOf(1,3,224,224)
            val inputTensor = Tensor.fromBlob(floatData, shape)
            val inputs = arrayOf(EValue.from(inputTensor))
            val outputs = model.forward(*inputs)[0].toTensor()

            val feat = outputs.dataAsFloatArray
            Pair(feat, data.Label)
        }

        for( epoch in 0 ..< config.epoch) {
            var totalLoss = 0f

            for ((features, label) in featureList) {
                // 2. Forward Pass (Classifier Layer)
                val logits = FloatArray(numClasses) { i ->
                    var sum = bias[i]
                    for (j in 0 until numFeatures) {
                        sum += features[j] * weights[i][j]
                    }
                    sum
                }

                // 3. Stable Softmax
                val maxLogit = logits.maxOrNull() ?: 0f
                val expScore = logits.map { kotlin.math.exp(it - maxLogit) }
                val sumExp = expScore.sum()
                val probs = expScore.map { (it / sumExp) }

                // 4. Cross Entropy Loss (Natural Log)
                var sampleLoss = 0f

                for (i in 0 until numClasses) {
                    // Label Smoothing target
                    val target = if (i == label) {
                        (1f - smoothingValue + (smoothingValue / numClasses))
                    } else {
                        (smoothingValue / numClasses)
                    }

                    // Kalkulasi Cross Entropy yang benar dengan Label Smoothing
                    sampleLoss += -target * ln(probs[i].coerceAtLeast(1e-10f))

                    val gradOut = probs[i] - target

                    // Update Bias
                    bias[i] -= learningRate * gradOut

                    // Update Weights
                    for (j in 0 until numFeatures) {
                        // PENTING: Normalisasi weight decay berdasarkan ukuran dataset
                        // agar tidak terlalu agresif saat menggunakan batch-size 1
                        val l2Reg = (weightDecay / dataset.size) * weights[i][j]
                        weights[i][j] -= learningRate * (gradOut * features[j] + l2Reg)
                    }
                }
                totalLoss += sampleLoss
                last3Loss.add(sampleLoss)


                // 5. Backpropagation
                for (i in 0 until numClasses) {
                    // Label Smoothing target
                    val target = if (i == label) (1f - smoothingValue + (smoothingValue / numClasses)) else (smoothingValue / numClasses)
                    val gradOut = probs[i] - target

                    // Update Bias
                    bias[i] -= learningRate * gradOut

                    // Update Weights with Weight Decay (L2)
                    for (j in 0 until numFeatures) {
                        val l2Reg = weightDecay * weights[i][j]
                        weights[i][j] -= learningRate * (gradOut * features[j] + l2Reg)
                    }
                }
            }
            val avgLoss = totalLoss / dataset.size
            onProgressUpdate(epoch, avgLoss)
            if(avgLoss < .2f){
                break;
            }
            if(abs(last3Loss.getList().get(0) - avgLoss) <= 0.003){
                totalEpoch = epoch
                break;
            }
            //Log.i("TRAIN", "Epoch ${epoch + 1} Done. Avg Loss: $avgLoss")
        }

        onFinished(totalEpoch)
        return mapOf("weights" to weights, "bias" to bias)
    }
}