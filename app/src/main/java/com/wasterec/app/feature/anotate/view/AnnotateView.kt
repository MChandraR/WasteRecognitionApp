package com.wasterec.app.feature.anotate.view

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.wasterec.app.feature.anotate.components.LabelSelectionComponent
import com.wasterec.app.feature.anotate.viewmodel.AnotateViewModel
import com.wasterec.app.feature.importimage.viewmodel.ImportImageViewModel
import com.wasterec.app.manager.EfficientNetB0
import com.wasterec.app.model.Destination
import com.wasterec.app.model.ModelConiguration
import com.wasterec.app.model.globalmodel.GlobalWeightModel
import com.wasterec.app.repositories.GlobalModelRepository
import com.wasterec.app.shared.components.MyButton
import com.wasterec.app.shared.components.SecondaryButton
import com.wasterec.app.ui.theme.Typography
import com.wasterec.app.utils.encodeWeightsToBase64
import com.wasterec.app.utils.floatArrayToBase64
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AnnotateView(
    anotateViewModel: AnotateViewModel? = null,
    importImageViewModel: ImportImageViewModel? = null,
) {

    Box{
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.weight(1f))

            Text(
                "Proses anotasi :  ${(anotateViewModel?.currentAnotateIndex?.value ?: 0) + 1} dari ${importImageViewModel?.imageDatasetList?.size}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            Text(
                "Pilih label kelas yang benar untuk gambar dibawah",
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 20.dp)
            )


            Spacer(modifier = Modifier.height(20.dp))

            LaunchedEffect(Dispatchers.IO) {
                anotateViewModel?.currentAnotateIndex?.value = 0
                println("Jumlah datset diimport" + importImageViewModel?.imageDatasetList?.size.toString())
            }


            if(importImageViewModel?.getImageDataBitmap(anotateViewModel?.currentAnotateIndex?.value?: 0) != null) {
                importImageViewModel.getImageDataBitmap(anotateViewModel?.currentAnotateIndex?.value?: 0)
                    ?.let { bmp ->
                        // Jalankan inference di background saat URI berubah
                        LaunchedEffect(Dispatchers.IO) {
                            anotateViewModel?.classifyImage(bmp)

                        }
                        anotateViewModel?.currentBitmap?.value = bmp
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(
                                bitmap = bmp.asImageBitmap(),
                                contentDescription = "Preview",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp)
                                    .padding(top = 16.dp)
                                    .background(Color.Transparent)
                                    .clip(RoundedCornerShape(10)),
                                contentScale = ContentScale.Crop
                            )

                        }
                    }
            }else{
                anotateViewModel?.predictResult?.value = "Failed to convert bitmap"
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .height(300.dp)
                )
            }




            if ( anotateViewModel?.predictResult != null) {
                Text(
                    anotateViewModel.predictResult.value ,
                    textAlign = TextAlign.Center,
                    fontSize = Typography.titleLarge.fontSize,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
                )

                Row(
                    modifier = Modifier
                        .padding(vertical = 20.dp)
                ) {
                    Text(
                        "Confident Lv.",
                        fontSize = Typography.titleLarge.fontSize
                    )
                    Text(
                        (anotateViewModel.confidentLevel.value?.times(100f)).toString()+"%",
                        fontSize = Typography.titleLarge.fontSize,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ){
                MyButton(
                    onClick = {
//                    if(currentBitmap != null){
//                        classifyImage(currentBitmap)
//                    }
                    },
                    modifier = Modifier
                        .background(
                            color = Color.LightGray,
                            shape = RoundedCornerShape(10)
                        )
                        .fillMaxWidth()
                ) {
                    Text("Klasifikasi",
                        modifier = Modifier.padding(10.dp))
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row {
                    SecondaryButton (
                        onClick = {
                            importImageViewModel?.setLabelForImage(anotateViewModel?.currentAnotateIndex?.value?:0, anotateViewModel?.predictedLabel?.value?:0)
                            println("Ditandai sebagai benar dengan label ${anotateViewModel?.predictResult?.value} index : ${anotateViewModel?.predictedLabel?.value}")
                            if(anotateViewModel != null){
                                anotateViewModel.currentAnotateIndex.value += 1
                                anotateViewModel.currentBitmap.value?.let { bitmap ->
                                    anotateViewModel.classifyImage(bitmap)
                                }
                            }
                        },
                        modifier = Modifier.weight(.5f)
                    ) {
                        Text("Benar",
                            modifier = Modifier.padding(5.dp))
                    }

                    Spacer(modifier = Modifier.weight(.1f))

                    SecondaryButton(
                        onClick = {
                            anotateViewModel?.showLabelSelectionMenu?.value = true
                        },
                        modifier = Modifier.weight(.5f)

                    ) {
                        Text("Salah",
                            modifier = Modifier.padding(5.dp))
                    }
                }


            }

            Spacer(Modifier.weight(1f))

            MyButton(
                onClick = {
                    anotateViewModel?.navHostController?.navigate(Destination.Training)
                },
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Text(
                    "Selanjutnya",
                    modifier = Modifier.padding(10.dp)
                )
            }

        }

        if(anotateViewModel?.showLabelSelectionMenu?.value == true){
            LabelSelectionComponent(modifier = Modifier) { selectedIndex ->
                importImageViewModel?.setLabelForImage(anotateViewModel.currentAnotateIndex.value, selectedIndex)
                anotateViewModel.showLabelSelectionMenu.value = false
            }
        }

    }


}



@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun Prev(){
    val importImageViewModel : ImportImageViewModel = viewModel()
    val anotateViewModel : AnotateViewModel = viewModel()

    AnnotateView(anotateViewModel, importImageViewModel)
}