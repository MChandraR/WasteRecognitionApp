package com.wasterec.app.feature.training.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.navigation.NavHostController
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.wasterec.app.feature.importimage.viewmodel.ImportImageViewModel
import com.wasterec.app.manager.EfficientNetB0
import com.wasterec.app.model.ModelConiguration
import com.wasterec.app.model.globalmodel.GlobalWeightModel
import com.wasterec.app.repositories.GlobalModelRepository
import com.wasterec.app.utils.encodeWeightsToBase64
import com.wasterec.app.utils.floatArrayToBase64
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TrainingViewModel(
    application : Application,
    val context : Context,
    val navHostController: NavHostController,
    val importImageViewModel: ImportImageViewModel
) : AndroidViewModel(application) {
    val efficientNetB0 : EfficientNetB0 = EfficientNetB0(context)
    val modelProducer : MutableState<CartesianChartModelProducer> = mutableStateOf(
        CartesianChartModelProducer())
    var currentEpoch : MutableState<Int>  = mutableStateOf(0)
    var currentLoss : MutableState<Float> = mutableStateOf(0f)
    val lossList = mutableStateListOf<Float>()
    var modelConfig = ModelConiguration(
        learningRate = 0.001f,
        epoch = 30,
        batchSize = 2
    )
    @RequiresApi(Build.VERSION_CODES.O)
    //Fungsi buat memanggil model dan mulai training local
    fun startLocalTraining(){
        CoroutineScope(Dispatchers.IO).launch {
            val data = efficientNetB0.train(
                config = modelConfig,
                dataset = importImageViewModel.imageDatasetList,
                onProgressUpdate = { epoch, loss ->
                    println("Progress pelatihan ${epoch}")
                    currentEpoch.value = epoch+1
                    currentLoss.value = loss
                    if (!loss.isNaN() && !loss.isInfinite()) {
                        lossList.add(loss)
                        CoroutineScope(Dispatchers.IO).launch {
                            modelProducer.value.runTransaction {
                                lineSeries {
                                    // Vico menerima List untuk X dan List untuk Y
                                    series(
                                        x = lossList.indices.toList(), // x = 0, 1, 2, ...
                                        y = lossList                   // y = nilai loss
                                    )
                                }
                            }
                        }
                    }

                    // 3. Update Vico Chart
                    // Gunakan tryRunTransaction agar thread-safe dan efisien



                }
            )

            println(data.get("weights"))

            GlobalModelRepository().uploadModelWeight(globalWeightModel = GlobalWeightModel(
                num_sample = importImageViewModel.imageDatasetList.size,
                label_count = importImageViewModel.getEachLabelCount(),
                weights = encodeWeightsToBase64(data.get("weights") as Array<FloatArray>),
                bias = floatArrayToBase64(data.getValue("bias") as FloatArray),
            )
            )
        }
    }


}