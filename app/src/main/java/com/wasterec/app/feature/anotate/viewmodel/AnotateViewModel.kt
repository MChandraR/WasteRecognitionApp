package com.wasterec.app.feature.anotate.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.wasterec.app.manager.EfficientNetB0
import com.wasterec.app.repositories.GlobalModelRepository
import com.wasterec.app.utils.decodeBase64ToFloatArray
import com.wasterec.app.utils.decodeBase64ToWeights
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class AnotateViewModel(application : Application,val context: Context, val navHostController: NavHostController): AndroidViewModel(application = application) {
    var currentAnotateIndex : MutableState<Int> = mutableStateOf(0)
    var showLabelSelectionMenu : MutableState<Boolean> = mutableStateOf(false)
    var predictResult : MutableState<String> = mutableStateOf("")
    var confidentLevel : MutableState<Float> = mutableStateOf(0f)
    var predictedLabel : MutableState<Int> = mutableStateOf(0)
    var currentBitmap : MutableState<Bitmap?> = mutableStateOf(null)
    var efficientNetB0 : EfficientNetB0? = EfficientNetB0(context, "model.ptl" )
    val label = arrayOf("Plastik", "Kertas", "Kaca",  "Logam", "Kardus", "Sampah")

    fun reInit(){
        currentBitmap.value = null
        currentAnotateIndex.value = 0
        showLabelSelectionMenu.value = false
        predictResult.value = ""
        confidentLevel.value = 0f
        predictedLabel.value = 0
        currentBitmap.value = null
        val globalModelFile = File(context.filesDir, "GlobalModel.ptl")
        if(globalModelFile.exists()){
            println("Ada file ${globalModelFile.absolutePath}")
            efficientNetB0 = EfficientNetB0(context, "GlobalModel.ptl")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun classifyImage(bmp : Bitmap){
        efficientNetB0?.let { efficientNetB0 ->
            CoroutineScope(Dispatchers.IO).launch{
                predictResult.value = "Loading..."
                withContext(Dispatchers.IO) {
                    val output = efficientNetB0.predict(
                        bmp.copy(Bitmap.Config.ARGB_8888, false)
                    )
                    val outputIdx = output.first
                    val labelResult = label.getOrNull(outputIdx)
                    withContext(Dispatchers.Main) {
                        predictResult.value = labelResult ?: "Unknown"
                        println("Label : " + labelResult)
                        println("Index Label : " + outputIdx)
                        confidentLevel.value = output.second
                        if (confidentLevel.value == 0.0f) {
                            predictResult.value = "Unknown"
                        }
                        predictedLabel.value = outputIdx
                        Toast.makeText(
                            context,
                            "Berhasil " + confidentLevel.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}