package com.wasterec.app.feature.modelload.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wasterec.app.feature.modelload.viewmodel.ModelLoadViewModel
import com.wasterec.app.ui.theme.Typography
import kotlinx.coroutines.Dispatchers
import kotlin.math.roundToInt

@Composable
fun ModelLoadView(modelLoadViewModel: ModelLoadViewModel?){

    //Init fungsi dan variabel dari viewModel
    LaunchedEffect(Dispatchers.IO) {
        modelLoadViewModel?.downloadBackboneModel()
        modelLoadViewModel?.loadClassifierParam()
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {


        Text("Load Backbone Model (${(modelLoadViewModel?.backboneModelDownloadProgress?.value?:0f).times(100).roundToInt() }%)",
            fontSize = Typography.bodyLarge.fontSize,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(20.dp)
        )

        LinearProgressIndicator(
            progress = {
                modelLoadViewModel?.backboneModelDownloadProgress?.value?:0f
            },
            modifier = Modifier.height(10.dp)
        )


        //Section untuk menampilkan status classifier weight yang udah diload
        Text("Load Classifier Weight (${(modelLoadViewModel?.classifierWeightDownloadProgress?.value?:0f).times(100).roundToInt() }%)",
            fontSize = Typography.bodyLarge.fontSize,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(20.dp)
        )

        LinearProgressIndicator(
            progress = {
                modelLoadViewModel?.classifierWeightDownloadProgress?.value?:0f
            },
            modifier = Modifier.height(10.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ModelLoadViewPreiew(){
    ModelLoadView(null)
}