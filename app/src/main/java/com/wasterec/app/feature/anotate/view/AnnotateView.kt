package com.wasterec.app.feature.anotate.view

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
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import com.wasterec.app.feature.anotate.components.LabelSelectionComponent
import com.wasterec.app.feature.anotate.components.ModelLoadingComponent
import com.wasterec.app.feature.anotate.viewmodel.AnnotateViewModel
import com.wasterec.app.feature.importimage.viewmodel.ImportImageViewModel
import com.wasterec.app.model.Destination
import com.wasterec.app.shared.components.AlertWithConfirmation
import com.wasterec.app.shared.components.MyButton
import com.wasterec.app.shared.components.SecondaryButton
import com.wasterec.app.ui.color.ColorAsset
import com.wasterec.app.ui.theme.Typography
import kotlinx.coroutines.Dispatchers
import kotlin.math.min

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AnnotateView(
    annotateViewModel: AnnotateViewModel? = null) {

    BackHandler() {
        annotateViewModel?.showCancellationConfirmationDialog?.value = true
    }

    LaunchedEffect(Dispatchers.IO) {
        //anotateViewModel?.getLatestGlobalWeight()
        annotateViewModel?.reInit()
    }

    Box{
        if(!(annotateViewModel?.isModelLoading?.value?:false)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(Modifier.weight(1f))

                Text(
                    "Proses anotasi :  ${(annotateViewModel?.currentAnnotateIndex?.value ?: 0) + 1} dari ${annotateViewModel?.datasetManager?.getDataSize()}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorAsset.primaryBlue,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                Text(
                    "Pilih label kelas yang benar untuk gambar dibawah",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                if (annotateViewModel?.datasetManager?.getImageDataBitmap(
                        annotateViewModel.currentAnnotateIndex?.value ?: 0
                    ) != null
                ) {
                    annotateViewModel?.datasetManager?.getImageDataBitmap(
                        annotateViewModel.currentAnnotateIndex?.value ?: 0
                    )
                        ?.let { bmp ->
                            // Jalankan inference di background saat URI berubah
                            LaunchedEffect(Dispatchers.IO) {
                                annotateViewModel?.classifyImage(bmp)
                            }
                            annotateViewModel?.currentBitmap?.value = bmp
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
                } else {
                    annotateViewModel?.predictResult?.value = "Failed to convert bitmap"
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .height(300.dp)
                    )
                }

                if (annotateViewModel?.predictResult != null) {
                    Text(
                        annotateViewModel.predictResult.value,
                        textAlign = TextAlign.Center,
                        fontSize = Typography.titleLarge.fontSize,
                        fontWeight = FontWeight.Bold,
                        color = ColorAsset.primaryBlue,
                        modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
                    )

                    Row(
                        modifier = Modifier
                            .padding(vertical = 20.dp)
                    ) {
                        Text(
                            "Confident Lv : ",
                            fontSize = Typography.titleLarge.fontSize
                        )
                        Text(
                            ( if (annotateViewModel.confidentLevel.value != 0f)  annotateViewModel.getConfidentLevelString() else "-"),
                            fontSize = Typography.titleLarge.fontSize,
                            color = ColorAsset.darkerBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(Modifier.weight(1f))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    MyButton(
                        onClick = {
                            annotateViewModel?.currentBitmap?.value?.let{ bmp->
                                annotateViewModel.classifyImage(bmp)
                            }
                        },
                        enabled = !(annotateViewModel?.isOnInference?.value ?: false),
                        modifier = Modifier
                            .background(
                                color = Color.LightGray,
                                shape = RoundedCornerShape(10)
                            )
                            .fillMaxWidth()
                    ) {
                        Text(
                            "Klasifikasi",
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row {
                        SecondaryButton(
                            onClick = {
                                annotateViewModel?.rightLabelCount?.intValue += 1
                                annotateViewModel?.datasetManager?.setLabelForImage(
                                    annotateViewModel.currentAnnotateIndex?.value ?: 0,
                                    annotateViewModel.predictedLabel?.value ?: 0
                                )
                                println("Ditandai sebagai benar dengan label ${annotateViewModel?.predictResult?.value} index : ${annotateViewModel?.predictedLabel?.value}")
                                val nextIndex = min(
                                    (annotateViewModel?.datasetManager?.getDataSize() ?: 0) - 1,
                                    (annotateViewModel?.currentAnnotateIndex?.value ?: 0) + 1
                                )
                                if (annotateViewModel != null) {

                                    annotateViewModel?.datasetManager?.trainingData?.get(nextIndex)?.Input?.let { bitmap ->
                                        annotateViewModel.classifyImage(bitmap)
                                    }
                                }
                                //Validasi jika index terakhir / gambar terakhir maka lanjut ke halaman selanjutnya
                                if ((annotateViewModel?.currentAnnotateIndex?.value
                                        ?: 0) >= (annotateViewModel?.datasetManager?.getDataSize()
                                        ?: 0) - 1
                                ) {
                                    annotateViewModel?.navHostController?.navigate(Destination.Training)
                                }
                                annotateViewModel?.currentAnnotateIndex?.value = nextIndex
                            },
                            enabled = !(annotateViewModel?.isOnInference?.value ?: false),
                            borderColor = ColorAsset.primaryGreen,
                            borderSize = 2,
                            modifier = Modifier.weight(.5f)
                        ) {
                            Text(
                                "Benar",
                                fontWeight = FontWeight.Bold,
                                color = ColorAsset.primaryGreen,
                                modifier = Modifier.padding(5.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(20.dp))

                        SecondaryButton(
                            onClick = {
                                annotateViewModel?.showLabelSelectionMenu?.value = true
                            },
                            enabled = !(annotateViewModel?.isOnInference?.value ?: false),
                            borderColor = ColorAsset.primaryRed,
                            borderSize = 2,
                            modifier = Modifier.weight(.5f)

                        ) {
                            Text(
                                "Salah",
                                fontWeight = FontWeight.Bold,
                                color = ColorAsset.primaryRed,
                                modifier = Modifier.padding(5.dp)
                            )
                        }
                    }


                }

                Spacer(Modifier.weight(1f))

            }
        }

        if(annotateViewModel?.showLabelSelectionMenu?.value == true){
            Column(
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize().background(ColorAsset.alertMainBg)
            ) {
                LabelSelectionComponent(modifier = Modifier.background(Color.White, shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))) { selectedIndex ->
                    println("Label yang benar sudah paperbacking $selectedIndex")
                    annotateViewModel.datasetManager.setLabelForImage(
                        annotateViewModel.currentAnnotateIndex.value,
                        selectedIndex
                    )
                   val nextIndex = min(
                        (annotateViewModel.datasetManager.getDataSize() ?: 0) - 1,
                        annotateViewModel.currentAnnotateIndex.value + 1
                    )
                    annotateViewModel.showLabelSelectionMenu.value = false
                    annotateViewModel.datasetManager?.trainingData?.get(nextIndex)?.Input?.let { bitmap ->
                        annotateViewModel.classifyImage(bitmap)
                    }

                    //Validasi , jika sudah di akhir index / gambar , lanjut ke training
                    if ((annotateViewModel.currentAnnotateIndex.value
                                ) >= (annotateViewModel.datasetManager.getDataSize() ?: 0) - 1
                    ) {
                        annotateViewModel.navHostController.navigate(Destination.Training)
                    }
                    annotateViewModel.currentAnnotateIndex.value = nextIndex
                }
            }

        }

        if(annotateViewModel?.isModelLoading?.value == true){
            ModelLoadingComponent()
        }

        AlertWithConfirmation(
            title = "Keluar proses annotasi ?",
            message = "Apakah anda yakin ingin kembail ? Anda harus mengulang proses annotasi dari awal.",
            showAlert = annotateViewModel?.showCancellationConfirmationDialog?.value ?: false,
            onConfirm = {
                annotateViewModel?.let {
                    annotateViewModel.showCancellationConfirmationDialog.value = false
                    annotateViewModel.navigateBack()
                }
            },
            onCancel = {
                annotateViewModel?.showCancellationConfirmationDialog?.value = false
            }
        )

    }


}



@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun Prev(){
    AnnotateView()
}