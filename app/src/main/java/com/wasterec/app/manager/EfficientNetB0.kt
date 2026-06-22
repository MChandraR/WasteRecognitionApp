package com.wasterec.app.manager

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.wasterec.app.model.ClassifierWeightModel
import com.wasterec.app.model.ModelConfiguration
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
            for (j in 0 until  1280 ){
                logit += features[j] * classifierParam.weights[i][j]
                //println(features[j] * classifierParam.first[i][j])
            }
            //println(logit)
            logit
        }

        val maxLogits = logits.maxOrNull() ?: 0f
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
            return Pair(-1, 0f)
        }


        val outputIdx = probabilities.indices.maxByOrNull { probabilities[it] } ?: -1
        val confidence = if (outputIdx != -1) probabilities[outputIdx] else 0f


        return Pair(outputIdx, confidence)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun evaluate(dataset: List<TrainingModel>): Pair<Float, Float> {
        val weights = this.classifierWeights
        val bias = this.classifierBias

        // Validasi jika bobot belum diinisialisasi
        if (weights == null || bias == null || dataset.isEmpty()) return Pair(0f, 0f)

        val numClasses = weights.size
        var totalLoss = 0f
        var correctPredictions = 0

        // 1. Ekstraksi fitur (Caching) agar evaluasi berjalan sangat cepat
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

        // 2. Proses Evaluasi dengan Linear Classifier (Softmax & Cross Entropy)
        for ((feature, label) in featureList) {
            // Hitung Logits
            val logits = FloatArray(numClasses) { i ->
                var sum = bias[i]
                for (j in 0 until feature.size) {
                    sum += feature[j] * weights[i][j]
                }
                sum
            }

            // Stable Softmax
            val maxLogit = logits.maxOrNull() ?: 0f
            val expScore = logits.map { kotlin.math.exp(it - maxLogit) }
            val totalScore = expScore.sum()
            val probs = expScore.map { it / totalScore }

            // Tentukan Prediksi Kelas (Index dengan probabilitas tertinggi)
            val predictedLabel = probs.indices.maxByOrNull { probs[it] } ?: -1
            if (predictedLabel == label) {
                correctPredictions++
            }

            // Hitung Loss (Cross Entropy) untuk label target asli
            for (i in 0 until numClasses) {
                if (i == label) {
                    totalLoss += -1f * ln(probs[i].coerceAtLeast(1e-10f))
                }
            }
        }

        // 3. Hitung Rata-rata Loss dan Akurasi
        val avgLoss = totalLoss / dataset.size
        val accuracy = correctPredictions.toFloat() / dataset.size

        Log.i("EVALUATE", "Evaluation Done. Accuracy: ${accuracy * 100}%, Avg Loss: $avgLoss")

        // Mereturn Pair(Akurasi, Rata-rata Loss)
        return Pair(accuracy, avgLoss)
    }

    fun setClassifierWeightAndBias(newWeight : Array<FloatArray>, newBias : FloatArray){
        this.setClassifierBias(newBias)
        this.setClassifierWeight(newWeight)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun train(config: ModelConfiguration, dataset: List<TrainingModel>, onProgressUpdate : (epoch:Int, loss : Float)->Unit, onFinished : (totalEpoch:Int, data : Map<String, Any>? )->Unit ): Map<String, Any> {
        var weights = this.classifierWeights
        var bias = this.classifierBias

        if (weights == null || bias == null) return emptyMap()
        var totalEpoch = config.epoch

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
            val batches = featureList.shuffled().chunked(config.batchSize)

            for(batch in batches){
                val weightGrad = Array<FloatArray>(numClasses){ FloatArray(numFeatures) }
                val biasGrad = FloatArray(numClasses)
                var batchLoss = 0f

                for((feature, label) in batch){
                    val logits = FloatArray( numClasses){
                        var sum = bias[it]
                        for(j in 0 until feature.size){
                            sum += feature[j] * weights[it][j]
                        }
                        sum
                    }

                    val maxLogit = logits.maxOrNull() ?: 0f
                    val expScore = logits.map{ kotlin.math.exp(it - maxLogit) }
                    val totalScore = expScore.sum()
                    val probs = expScore.map{it/totalScore}

                    for(i in 0 until numClasses){
                        val target =
                            if(i==label){
                                1f
                            }else{
                                0f
                            }

                        batchLoss += -target * ln(probs[i].coerceAtLeast(1e-10f))
                        val gradOut = probs[i] - target

                        biasGrad[i] += gradOut
                        for(j in 0 until numFeatures){
                            weightGrad[i][j] += (gradOut * feature[j])

                        }

                    }
                }

                val batchSize = batch.size.toFloat()
                for(i in 0 until numClasses){
                    bias[i] -= (learningRate * ( biasGrad[i] / batchSize));

                    for(j in 0 until numFeatures){
                        val avgGrad = (weightGrad[i][j]/batchSize)
                        weights[i][j] -= learningRate * avgGrad
                    }
                }
                totalLoss += batchLoss
            }

            val avgLoss = totalLoss / dataset.size
            onProgressUpdate(epoch, avgLoss)
            if(avgLoss <= .15f){
                break;
            }
//            if(abs(last3Loss.getList().get(0) - avgLoss) <= 0.003){
//                totalEpoch = epoch-1
//                break;
//            }
            Log.i("TRAIN", "Epoch ${epoch + 1} Done. Avg Loss: $avgLoss")
        }

        onFinished(totalEpoch,  mapOf("weights" to weights, "bias" to bias))
        return mapOf("weights" to weights, "bias" to bias)
    }

    fun trainWithFeature(featureList : List<Pair<FloatArray, Int>>, config: ModelConfiguration, dataset: List<TrainingModel>, onProgressUpdate : (epoch:Int, loss : Float)->Unit, onFinished : (totalEpoch:Int)->Unit = {}): Map<String, Any> {
        val weights = this.classifierWeights
        val bias = this.classifierBias

        if (weights == null || bias == null) return emptyMap()
        var totalEpoch = config.epoch



        val numClasses = weights.size
        val numFeatures = weights[0].size
        val learningRate = config.learningRate

        // 1. Ekstraksi fitur (Caching) - Backbone Frozen
        val featureList = featureList

        for( epoch in 0 ..< config.epoch) {
            var totalLoss = 0f
            var batches = featureList.shuffled().chunked(config.batchSize)

            for(batch in batches){
                var weightGrad = Array<FloatArray>(numClasses){ FloatArray(numFeatures) }
                var biasGrad = FloatArray(numClasses)
                var batchLoss = 0f

                for((feature, label) in batch){
                    val logits = FloatArray(numClasses) {
                        var sum = bias[it]
                        for (j in 0 until feature.size) {
                            sum += feature[j] * weights[it][j]
                        }
                        sum
                    }

                    val maxLogit = logits.maxOrNull() ?: 0f
                    val expScore = logits.map{ kotlin.math.exp(it - maxLogit) }
                    val totalScore = expScore.sum()
                    val probs = expScore.map{it/totalScore}

                    for(i in 0 until numClasses){
                        val target =
                            if(i==label){
                                1f
                            }else{
                                0f
                            }

                        batchLoss += -target * ln(probs[i].coerceAtLeast(1e-10f))
                        val gradOut = probs[i] - target

                        biasGrad[i] += gradOut
                        for(j in 0 until numFeatures){
                            weightGrad[i][j] += (gradOut * feature[j])

                        }

                    }
                }

                val batchSize = batch.size.toFloat()
                for(i in 0 until numClasses){
                    bias[i] -= (learningRate * ( biasGrad[i] / batchSize));

                    for(j in 0 until numFeatures){
                        val avgGrad = (weightGrad[i][j]/batchSize)
                        weights[i][j] -= learningRate * avgGrad
                    }
                }
                totalLoss += batchLoss
            }

            val avgLoss = totalLoss / dataset.size
            onProgressUpdate(epoch, avgLoss)
//            if(avgLoss < .2f){
//                break;
//            }

            Log.i("TRAIN", "Epoch ${epoch + 1} Done. Avg Loss: $avgLoss")
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