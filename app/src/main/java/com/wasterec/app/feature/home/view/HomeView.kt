package com.wasterec.app.feature.home.view

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.wasterec.app.R
import com.wasterec.app.feature.home.viewmodel.HomeViewModel
import com.wasterec.app.model.Destination
import com.wasterec.app.shared.components.GifLoader
import com.wasterec.app.ui.color.ColorAsset
import com.wasterec.app.ui.theme.Typography
import com.wasterec.app.ui.theme.lightGray

@Composable
fun HomeView(
    homeViewModel: HomeViewModel? = null,
    modifier: Modifier
){

    LaunchedEffect(Unit) {

        homeViewModel?.getGlobalModelInfo()
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp)
                .padding(horizontal = 20.dp)
                .scrollable(state = rememberScrollState(), orientation =  Orientation.Vertical),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))
            GifLoader(R.drawable.network)

//            Text(
//                "Waste Rec",
//                fontSize = Typography.displaySmall.fontSize,
//                fontFamily = Typography.titleLarge.fontFamily,
//                fontWeight = FontWeight.Bold,
//                color = ColorAsset.primaryBlue
//            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                "Statistik Global Model",
                textAlign = TextAlign.Start,
                fontSize = Typography.titleMedium.fontSize,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                color = ColorAsset.darkBg75
            )

            Row(
                modifier = Modifier
                    .background(
                        color = ColorAsset.alpha5,
                        shape = RoundedCornerShape(10)
                    )
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ){
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        "Akurasi :",
                    )
                    Text(
                        "80%",
                        fontWeight = FontWeight.Bold,
                        fontSize = Typography.titleLarge.fontSize,
                        modifier = Modifier
                            .padding(top=2.dp)
                            .padding(bottom = 20.dp),
                        color = ColorAsset.primaryBlue
                    )
                    Text(
                        "Model :",
                    )
                    Text(
                        (homeViewModel?.globalModelInfoModel?.value)?.model_name ?: "EfficientNet-B0" ,
                        fontWeight = FontWeight.Bold,
                        fontSize = Typography.titleSmall.fontSize,
                        modifier = Modifier
                            .padding(top=2.dp),
                        color = ColorAsset.primaryBlue
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        "Round :",
                    )
                    Text(
                        "1 (On Queue)",
                        fontWeight = FontWeight.Bold,
                        fontSize = Typography.titleSmall.fontSize,
                        modifier = Modifier
                            .padding(top=2.dp)
                            .padding(bottom = 20.dp),
                        color = ColorAsset.primaryBlue
                    )
                    Text(
                        "Terakhir Diperbarui :",
                    )
                    Text(
                        (homeViewModel?.globalModelInfoModel?.value)?.last_updated ?: "-" ,
                        fontWeight = FontWeight.Bold,
                        fontSize = Typography.titleSmall.fontSize,
                        modifier = Modifier
                                 .padding(top=2.dp),
                        color = ColorAsset.primaryBlue
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                colors = ButtonDefaults.buttonColors(ColorAsset.primaryBlue),
                shape = RoundedCornerShape(10.dp),
                onClick = {
                    println("Hallo")
                    homeViewModel?.navHostController?.navigate(route = Destination.WeightLoading)
                },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    "Start Training",
                    modifier = Modifier.padding(10.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }


    }
}

@Preview(showBackground = true)
@Composable
fun homeViewPreview(){
    HomeView(modifier = Modifier)
}