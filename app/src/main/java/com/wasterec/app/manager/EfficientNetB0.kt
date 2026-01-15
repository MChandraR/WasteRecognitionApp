package com.wasterec.app.manager

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.wasterec.app.model.ModelConiguration
import com.wasterec.app.model.TrainingModel
import com.wasterec.app.utils.forceSoftwareBitmap
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.torchvision.TensorImageUtils
import kotlin.math.ln
import kotlin.math.log

class EfficientNetB0(val context: Context, val modelPath : String = "backbone.ptl") : ModelManager(context, modelPath) {
    val model: Module = this.loadModel()

    @RequiresApi(Build.VERSION_CODES.O)
    fun predict(bitmap: Bitmap) : Pair<Int, Float>{
        val safeBitmap = if (bitmap.config == Bitmap.Config.HARDWARE) {
            bitmap.copy(Bitmap.Config.ARGB_8888, false)
        } else {
            bitmap
        }

        val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(safeBitmap,
            TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
            TensorImageUtils.TORCHVISION_NORM_STD_RGB
            )
        val outputTensor = model.forward(IValue.from(inputTensor)).toTensor()
        val scores = outputTensor.dataAsFloatArray
        val output = scores.indices.maxByOrNull { scores[it] }
        val expScores = scores.map { kotlin.math.exp(it) }
        val sumExp = expScores.sum()
        val probabilities = expScores.map { it / sumExp }

        // Ambil index dengan nilai tertinggi
        val outputIdx = probabilities.indices.maxByOrNull { probabilities[it] } ?: -1
        val confidence = if (outputIdx != -1) probabilities[outputIdx] else 0f

        return Pair(outputIdx, confidence)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun train(config: ModelConiguration, dataset: List<TrainingModel>, onProgressUpdate : (epoch:Int, loss : Float)->Unit): Map<String, Any> {
        println("Jumlah data " + dataset.size.toString())
        // Ambil parameter classifier dari model manager
        var weights = this.weights     // Array<FloatArray>
        var bias = this.bias           // FloatArray

        if (weights == null || bias == null) {
            Log.e("Train", "Weights or bias not initialized!")
            return emptyMap()
        }

        val numClasses = weights.size          // baris = jumlah kelas
        val numFeatures = weights[0].size      // kolom = jumlah fitur dari backbone
        val learningRate = config.learningRate

        repeat(config.epoch) { epoch ->
            var totalLoss = 0f

            for (data in dataset) {
                // 1️⃣ Konversi bitmap jadi tensor
                val safeBitmap = if (data.Input.config == Bitmap.Config.HARDWARE) {
                    data.Input.copy(Bitmap.Config.ARGB_8888, false)
                } else {
                    data.Input
                }

                val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
                   safeBitmap,
                    floatArrayOf(0.485f, 0.456f, 0.406f),
                    floatArrayOf(0.229f, 0.224f, 0.225f)
                )

                // 2️⃣ Dapatkan fitur dari model backbone
                val outputTensor = this.model.forward(IValue.from(inputTensor)).toTensor()
                val features = outputTensor.dataAsFloatArray

                System.out.println("Panjang feature ${features.size}")
                System.out.println("Panjang feature ${weights.size}")
                System.out.println("Panjang feature ${weights[0].size}")

                // 3️⃣ Forward ke classifier manual
                val logits = FloatArray(numClasses) { i ->
                    var sum = bias[i]
                    for (j in 0 until numFeatures) {
                        sum += features[j] * weights[i][j]
                        //println("${i} daaan ${j}")
                    }
                    println("Summm ${sum}")

                    sum
                }

                println("Total sum : ${logits}")
                val maxLogit = logits.maxOrNull()!!
                val stable = logits.map { it - maxLogit }.toFloatArray()
                val probs = softmax(stable)

                val loss = -ln(probs[data.Label])
                totalLoss += loss

                // 5️⃣ Hitung gradien output
                val gradOut = FloatArray(numClasses)
                for (i in 0 until numClasses) {
                    gradOut[i] = probs[i] - if (i == data.Label) 1f else 0f
                }

                // 6️⃣ Update parameter classifier (SGD)
                for (i in 0 until numClasses) {
                    bias[i] -= learningRate * gradOut[i]
                    for (j in 0 until numFeatures) {
                        weights[i][j] -= learningRate * gradOut[i] * features[j]
                    }
                }
            }
            onProgressUpdate(epoch, totalLoss)
            Log.i("TRAIN", "Epoch ${epoch + 1} avg loss = ${totalLoss / dataset.size}")
        }

        // 7️⃣ Return parameter classifier hasil update (untuk federated update ke server)
        return mapOf(
            "weights" to weights,
            "bias" to bias
        )
    }

}