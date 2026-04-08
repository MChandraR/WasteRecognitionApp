package com.wasterec.app.feature.modelload.view

import android.widget.Space
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wasterec.app.feature.modelload.viewmodel.ModelLoadViewModel
import com.wasterec.app.ui.color.ColorAsset
import com.wasterec.app.ui.theme.Typography
import kotlinx.coroutines.Dispatchers
import kotlin.math.roundToInt

@Composable
fun ModelLoadView(modelLoadViewModel: ModelLoadViewModel?){

    //Init fungsi dan variabel dari viewModel
    LaunchedEffect(Dispatchers.IO) {
        modelLoadViewModel?.getGlobalModel()
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().padding(20.dp)
    ) {
        Spacer(modifier = Modifier.weight(.3f))

        Text("Memuat Model Global",
            fontSize = Typography.titleLarge.fontSize,
            fontWeight = FontWeight.Bold,
            color = ColorAsset.primaryBlue,
        )

        Spacer(modifier = Modifier.weight(.4f))

        Text("Memuat model terbaru dari server",
            fontSize = Typography.titleSmall.fontSize,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Start,
            color = ColorAsset.darkerBlue,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
        )




        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                "Backbone Model (${
                    (modelLoadViewModel?.backboneModelDownloadProgress?.value ?: 0f).times(
                        100
                    ).roundToInt()
                }%)",
                fontSize = Typography.bodyLarge.fontSize,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 20.dp).fillMaxWidth()
            )

            LinearProgressIndicator(
                color = ColorAsset.primaryBlue,
                trackColor = ColorAsset.primaryBlue25,
                strokeCap = StrokeCap.Round,
                gapSize = 0.dp,
                progress = {
                    modelLoadViewModel?.backboneModelDownloadProgress?.value ?: 0f
                },
                modifier = Modifier.height(15.dp).fillMaxWidth(),
                drawStopIndicator = {}
            )


            //Section untuk menampilkan status classifier weight yang udah diload
            Text(
                "Classifier Weight (${
                    (modelLoadViewModel?.classifierWeightDownloadProgress?.value ?: 0f).times(
                        100
                    ).roundToInt()
                }%)",
                fontSize = Typography.bodyLarge.fontSize,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 20.dp).fillMaxWidth()
            )

            LinearProgressIndicator(
                color = ColorAsset.primaryBlue,
                trackColor = ColorAsset.primaryBlue25,
                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                gapSize = 0.dp,
                progress = {
                    modelLoadViewModel?.classifierWeightDownloadProgress?.value ?: 0f
                },
                modifier = Modifier.height(15.dp).fillMaxWidth(),
                drawStopIndicator = {}
            )
        }

        Text("* Harap jangan tutup halaman ini hingga proses selesai",
            fontSize = Typography.bodyLarge.fontSize,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Start,
            color = ColorAsset.primaryRed,
            modifier = Modifier.fillMaxWidth().padding(horizontal =20.dp).padding(top=20.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

    }
}

@Preview(showBackground = true)
@Composable
fun ModelLoadViewPreiew(){
    ModelLoadView(null)
}