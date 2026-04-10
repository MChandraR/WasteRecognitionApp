package com.wasterec.app.manager

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import com.wasterec.app.model.ClassifierWeightModel
import com.wasterec.app.model.ModelConiguration
import com.wasterec.app.model.SlidingArray
import com.wasterec.app.model.TrainingModel
import com.wasterec.app.utils.forceSoftwareBitmap
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.Tensor
import org.pytorch.torchvision.TensorImageUtils
import kotlin.math.abs
import kotlin.math.ln

class EfficientNetB0(val context: Context,  modelPath : String = "backbone.ptl") : ModelManager(context, modelPath) {
    val model: Module = this.loadModel()

    @RequiresApi(Build.VERSION_CODES.O)
    fun predict(bitmap: Bitmap) : Pair<Int, Float>{
        // ... (kode bitmap to tensor Anda tetap sama)
        val safeBitmap = forceSoftwareBitmap(bitmap)

        val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(safeBitmap,
            TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
            TensorImageUtils.TORCHVISION_NORM_STD_RGB
        )
        val outputTensor = model.forward(IValue.from(inputTensor)).toTensor()
        val scores = outputTensor.dataAsFloatArray

        scores.forEach{
            println(it)
        }

        // Validasi apakah model mengeluarkan NaN
        if (scores.any { it.isNaN() }) {
            println("ERROR: Model output contains NaN")
            return Pair(-1, 0f)
        }

        // STABLE SOFTMAX IMPLEMENTATION
        val maxLogit = scores.maxOrNull() ?: 0f
        val expScores = scores.map { kotlin.math.exp(it - maxLogit) }
        val sumExp = expScores.sum()

        val probabilities = expScores.map { it / sumExp }

        val outputIdx = probabilities.indices.maxByOrNull { probabilities[it] } ?: -1
        val confidence = if (outputIdx != -1) probabilities[outputIdx] else 0f


        return Pair(outputIdx, confidence)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun backbonePredict(bitmap: Bitmap) : Pair<Int, Float>{
        // ... (kode bitmap to tensor Anda tetap sama)
        val safeBitmap = forceSoftwareBitmap(bitmap)
        var classifierParam  = ClassifierWeightModel(arrayOf(floatArrayOf()), floatArrayOf())

        loadClassifierParams{
            classifierParam =  ClassifierWeightModel(it.first, it.second)
        }

        val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(safeBitmap,
            TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
            TensorImageUtils.TORCHVISION_NORM_STD_RGB
        )
        val outputTensor = model.forward(IValue.from(inputTensor)).toTensor()
        val features = outputTensor.dataAsFloatArray
        println(features.size)

        val logits = FloatArray(classifierParam.bias.size){ i ->
            var logit : Float = classifierParam.bias[i]
            println("==========================")
            for (j in 0 until  1280 ){
                logit += features[j] * classifierParam.weights[i][j]
                //println(features[j] * classifierParam.first[i][j])
            }
            //println(logit)
            logit
        }

        val maxLogits = logits.maxOrNull() ?: 0f
        println("Max : $maxLogits")
        val expLogits = logits.map{ kotlin.math.exp(it - maxLogits) }
        expLogits.forEach {
            println(it)
        }
        val totalExpLogits = expLogits.sum()

        val probabilities = expLogits.map{it/totalExpLogits}

        probabilities.forEach {
            //println(it)
        }

        // Validasi apakah model mengeluarkan NaN
        if (features.any { it.isNaN() }) {
            println("ERROR: Model output contains NaN")
            return Pair(-1, 0f)
        }


        val outputIdx = probabilities.indices.maxByOrNull { probabilities[it] } ?: -1
        val confidence = if (outputIdx != -1) probabilities[outputIdx] else 0f


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

            val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
                safeBitmap,
                TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
                TensorImageUtils.TORCHVISION_NORM_STD_RGB
            )
            val feat = this.model.forward(IValue.from(inputTensor)).toTensor().dataAsFloatArray
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
                totalEpoch = epoch-1
                break;
            }
            //Log.i("TRAIN", "Epoch ${epoch + 1} Done. Avg Loss: $avgLoss")
        }

        onFinished(totalEpoch)
        return mapOf("weights" to weights, "bias" to bias)
    }

    fun updateClassifierWeight(weights: Array<FloatArray>, bias: FloatArray) {
        // 1. Validasi dimensi (Opsional tapi bagus untuk debugging)
        val rows = weights.size        // 6
        val cols = weights[0].size     // 1280

        // 2. RATAKAN (FLATTEN) Array<FloatArray> menjadi satu FloatArray panjang
        // Kita buat wadah kosong dengan ukuran total (6 * 1280)
        val flatWeights = FloatArray(rows * cols)

        // Salin data baris per baris ke array datar
        for (i in weights.indices) {
            // System.arraycopy(sumber, mulai_sumber, tujuan, mulai_tujuan, panjang)
            System.arraycopy(weights[i], 0, flatWeights, i * cols, cols)
        }

        // 3. Buat Tensor dari Array yang sudah datar
        // Masukkan flatWeights, tapi berikan shape [6, 1280] agar PyTorch tahu cara melipatnya
        val weightTensor = Tensor.fromBlob(flatWeights, longArrayOf(rows.toLong(), cols.toLong()))

        // Bias biasanya sudah flat, jadi aman
        val biasTensor = Tensor.fromBlob(bias, longArrayOf(rows.toLong()))

        // 4. Eksekusi Method
        // Perbaikan: IValue.from() tidak boleh kosong, harus diisi Tensor
        this.model.runMethod(
            "update_last_layer",
            IValue.from(weightTensor), // Masukkan Tensor Bobot
            IValue.from(biasTensor)    // Masukkan Tensor Bias
        )
    }


}