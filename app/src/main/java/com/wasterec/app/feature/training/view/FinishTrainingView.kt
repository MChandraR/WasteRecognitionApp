package com.wasterec.app.feature.training.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.wasterec.app.R
import com.wasterec.app.feature.training.viewmodel.FinishTrainingViewModel
import com.wasterec.app.model.Destination
import com.wasterec.app.shared.components.MyButton
import com.wasterec.app.ui.color.ColorAsset
import com.wasterec.app.ui.theme.Typography

@Composable
fun FinishTrainingView(
    finishTrainingViewModel: FinishTrainingViewModel? = null
){

    BackHandler() {
        finishTrainingViewModel?.navigateBackToHome()
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ){
        Spacer(Modifier.weight(1f))


        Image(
            painter = painterResource(R.drawable.cloud),
            "Cloud",
            modifier = Modifier.width(100.dp).height(100.dp)
        )

        Text(
            "Pelatihan Selesai",
            color = ColorAsset.primaryBlue,
            fontSize = Typography.titleLarge.fontSize,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 10.dp)
        )

        Text(
            "Bobot hasil pelatihan telah diupload ke server",
            fontSize = Typography.titleSmall.fontSize,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal=20.dp)
        )


        Spacer(Modifier.weight(1f))


        MyButton(
            onClick = {
               finishTrainingViewModel?.navigateBackToHome()

            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 20.dp)
        ) {
            Text("Selesai",
                fontWeight = FontWeight.Bold,
                fontSize = Typography.bodyLarge.fontSize,
                modifier = Modifier.padding(10.dp)
                )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun Finish(){
    FinishTrainingView()
}