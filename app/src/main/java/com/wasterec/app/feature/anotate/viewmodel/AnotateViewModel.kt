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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AnotateViewModel(application : Application,val context: Context, val navHostController: NavHostController): AndroidViewModel(application = application) {
    var currentAnotateIndex : MutableState<Int> = mutableStateOf(0)
    var showLabelSelectionMenu : MutableState<Boolean> = mutableStateOf(false)
    var predictResult : MutableState<String> = mutableStateOf("")
    var confidentLevel : MutableState<Float> = mutableStateOf(0f)
    var predictedLabel : MutableState<Int> = mutableStateOf(0)
    var currentBitmap : MutableState<Bitmap?> = mutableStateOf(null)
    val efficientNetB0 : EfficientNetB0? = EfficientNetB0(context, "model.ptl" )
    val label = arrayOf("Glass", "Paper", "Cardboard",  "Metal","Plastic", "Trash")

    @RequiresApi(Build.VERSION_CODES.O)
    fun classifyImage(bmp : Bitmap){
        if (efficientNetB0 != null) {
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
                        if (confidentLevel.value == null || confidentLevel.value == 0.0f) {
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