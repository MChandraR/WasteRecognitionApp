package com.wasterec.app.feature.data_preprocessing.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.SliderDefaults.drawStopIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wasterec.app.R
import com.wasterec.app.shared.components.RoundedColoredBox
import com.wasterec.app.feature.data_preprocessing.viewmodel.DataProcessingViewModel
import com.wasterec.app.shared.components.GifLoader
import com.wasterec.app.shared.components.MyButton
import com.wasterec.app.ui.color.ColorAsset
import com.wasterec.app.ui.theme.Typography
import kotlinx.coroutines.Dispatchers

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DataProcessingView(
    dataProcessingViewModel: DataProcessingViewModel? = null
){
    LaunchedEffect(Dispatchers.IO) {
        dataProcessingViewModel?.reInit()
        dataProcessingViewModel?.proProcessData()
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize().padding(20.dp).background(Color.White)) {
        Spacer(modifier = Modifier.weight(.5f))
        Text("Pra-Pemrosesan Data",
            fontSize = Typography.displaySmall.fontSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = ColorAsset.primaryBlue,
            modifier = Modifier.padding(20.dp)
            )
        Spacer(modifier = Modifier.weight(.5f))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ){
                RoundedColoredBox("${(dataProcessingViewModel?.resizedCount?.intValue)?:0}", "Resized", color = ColorAsset.primaryGreen, modifier = Modifier.weight(.5f))
                RoundedColoredBox("${(dataProcessingViewModel?.rotatedCount?.intValue)?:0}", "Rotated", color = ColorAsset.primaryPurple,  modifier = Modifier.weight(.5f))
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ){
                RoundedColoredBox("${(dataProcessingViewModel?.horizontallyFlippedCount?.intValue)?:0}", "Flipped Horizontally", color = ColorAsset.primaryOrange, modifier = Modifier.weight(.5f))
                RoundedColoredBox("${(dataProcessingViewModel?.verticallyFlippedCount?.intValue)?:0}", "Flipped Vertically", color = ColorAsset.primaryYellow, modifier = Modifier.weight(.5f))
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        GifLoader(R.drawable.data_collection,
            modifier = Modifier
                .size(150.dp)
        )

        //Box(modifier = Modifier.size(20.dp).background(Color.Red))
        Spacer(modifier = Modifier.weight(1f))

        Column() {
            LinearProgressIndicator(
                progress = { (dataProcessingViewModel?.dataProcessingProgress?.floatValue)?:0f },
                modifier = Modifier.fillMaxWidth().height(20.dp).padding(bottom = 10.dp),
                color = ColorAsset.primaryBlue,
                trackColor = ColorAsset.primaryBlue25,
                gapSize = (-5).dp,
                drawStopIndicator = {
                    drawStopIndicator(Offset.Zero, 0.dp,Color.Transparent)
                }
            )

            MyButton(
                enabled = dataProcessingViewModel?.dataProcessingProgress?.floatValue == 1f,
                onClick = {
                    dataProcessingViewModel?.navigateToTrainingPage()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Selanjutnya",
                    modifier = Modifier.padding(vertical = 10.dp)
                )
            }
        }

    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun DataProcessingViewPreview(){
    DataProcessingView()
}