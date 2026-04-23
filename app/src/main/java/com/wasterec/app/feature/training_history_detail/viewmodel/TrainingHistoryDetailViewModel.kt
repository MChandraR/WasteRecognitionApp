package com.wasterec.app.feature.training_history_detail.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.navigation.NavHostController
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.wasterec.app.model.domain.TrainingData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TrainingHistoryDetailViewModel (
    val app: Application,
    val navHostController: NavHostController,
    val selectedTrainingHistory : MutableState<TrainingData>?
): AndroidViewModel(app){
    val label = arrayOf("Plastik", "Kertas", "Kaca",  "Logam", "Kardus", "Sampah")

    val modelProducer : MutableState<CartesianChartModelProducer> = mutableStateOf(
        CartesianChartModelProducer()
    )


    fun updateLossChartData(){
        selectedTrainingHistory?.value?.loss?.let { data ->
            (0 until data.size step 5).map { data[it] }.let { loss ->
                CoroutineScope(Dispatchers.IO).launch {
                    modelProducer.value.runTransaction {
                        lineSeries {
                            series(
                                x = loss.indices.toList().map { it * 5 },
                                y = loss
                            )
                        }
                    }
                }
            }
        }
    }

    fun deleteTrainingData(){
        Toast.makeText(app.baseContext, "Belum dapat menghapus data", Toast.LENGTH_SHORT).show()
    }
}