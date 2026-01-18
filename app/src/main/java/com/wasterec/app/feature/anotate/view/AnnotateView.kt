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
import androidx.annotation.RequiresApi
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import com.wasterec.app.feature.anotate.components.LabelSelectionComponent
import com.wasterec.app.feature.anotate.viewmodel.AnotateViewModel
import com.wasterec.app.feature.importimage.viewmodel.ImportImageViewModel
import com.wasterec.app.model.Destination
import com.wasterec.app.shared.components.MyButton
import com.wasterec.app.shared.components.SecondaryButton
import com.wasterec.app.ui.color.ColorAsset
import com.wasterec.app.ui.theme.Typography
import kotlinx.coroutines.Dispatchers
import kotlin.math.min

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AnnotateView(
    anotateViewModel: AnotateViewModel? = null,
    importImageViewModel: ImportImageViewModel? = null,
) {

    LaunchedEffect(Dispatchers.IO) {
        //anotateViewModel?.getLatestGlobalWeight()
        anotateViewModel?.reInit()
    }

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
                        (anotateViewModel.confidentLevel.value.times(100f)).toString()+"%",
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
                                anotateViewModel.currentAnotateIndex.value = min((importImageViewModel?.imageDatasetList?.size?:0) - 1 ,anotateViewModel.currentAnotateIndex.value+1 )
                                importImageViewModel?.imageDatasetList?.get(anotateViewModel.currentAnotateIndex.value)?.Input?.let { bitmap ->
                                    anotateViewModel.classifyImage(bitmap)
                                }
                            }
                            //Validasi jika index terakhir / gambar terakhir maka lanjut ke halaman selanjutnya
                            if( (anotateViewModel?.currentAnotateIndex?.value
                                    ?: 0) >= (importImageViewModel?.imageDatasetList?.size?:0)
                            ){
                                anotateViewModel?.navHostController?.navigate(Destination.Training)
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

        }

        if(anotateViewModel?.showLabelSelectionMenu?.value == true){
            Column(
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize().background(ColorAsset.alertMainBg)
            ) {
                LabelSelectionComponent(modifier = Modifier.background(Color.White, shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))) { selectedIndex ->
                    println("Label yang benar sudah paperbacking $selectedIndex")
                    importImageViewModel?.setLabelForImage(anotateViewModel.currentAnotateIndex.value, selectedIndex)
                    anotateViewModel.currentAnotateIndex.value = min((importImageViewModel?.imageDatasetList?.size?:0) - 1 ,anotateViewModel.currentAnotateIndex.value+1 )
                    anotateViewModel.showLabelSelectionMenu.value = false
                    importImageViewModel?.imageDatasetList?.get(anotateViewModel.currentAnotateIndex.value)?.Input?.let { bitmap ->
                        anotateViewModel.classifyImage(bitmap)
                    }
                    //Validasi , jika sudah di akhir index / gambar , lanjut ke training
                    if( (anotateViewModel.currentAnotateIndex.value
                            ) >= (importImageViewModel?.imageDatasetList?.size?:0)
                    ){
                        anotateViewModel.navHostController.navigate(Destination.Training)
                    }
                }
            }

        }

    }


}



@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun Prev(){
    AnnotateView()
}