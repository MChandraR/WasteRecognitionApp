package com.wasterec.app.feature.training.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.navigation.NavHostController
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

    @RequiresApi(Build.VERSION_CODES.O)
    //Fungsi buat memanggil model dan mulai training local
    fun startLocalTraining(){
        CoroutineScope(Dispatchers.IO).launch {
            val data = efficientNetB0.train(
                config = ModelConiguration(
                    learningRate = 0.001f,
                    epoch = 1,
                    batchSize = 2
                ),
                dataset = importImageViewModel.imageDatasetList
            )

            println(data.get("weights"))

            GlobalModelRepository().uploadModelWeight(globalWeightModel = GlobalWeightModel(
                weights = encodeWeightsToBase64(data.get("weights") as Array<FloatArray>),
                bias = floatArrayToBase64(data.getValue("bias") as FloatArray),
            )
            )
        }
    }

}