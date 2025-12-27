package com.wasterec.app.presentation.views

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.wasterec.app.R
import com.wasterec.app.model.Destination
import com.wasterec.app.presentation.components.GifLoader
import com.wasterec.app.ui.theme.Typography
import com.wasterec.app.ui.theme.lightGray

@Composable
fun HomeView(
    navController: NavHostController,
    modifier: Modifier
){
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

            Text(
                "Waste Rect",
                fontSize = Typography.titleLarge.fontSize,
                fontFamily = Typography.titleLarge.fontFamily,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                "Model State",
                textAlign = TextAlign.Start,
                fontSize = Typography.bodyLarge.fontSize,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            )

            Row(
                modifier = Modifier
                    .background(
                        color = lightGray,
                        shape = RoundedCornerShape(10)
                    )
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ){
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        "Accuracy",
                    )
                    Text(
                        "80%",
                        fontWeight = FontWeight.Bold,
                        fontSize = Typography.titleSmall.fontSize,
                        modifier = Modifier
                            .padding(bottom = 20.dp)
                    )
                    Text(
                        "Arch.",
                    )
                    Text(
                        "EfficientNet-B0",
                        fontWeight = FontWeight.Bold,
                        fontSize = Typography.titleSmall.fontSize,
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        "Loss",
                    )
                    Text(
                        "0.1",
                        fontWeight = FontWeight.Bold,
                        fontSize = Typography.titleSmall.fontSize,
                        modifier = Modifier
                            .padding(bottom = 20.dp)
                    )
                    Text(
                        "Last Training.",
                    )
                    Text(
                        "25 Sep 2025",
                        fontWeight = FontWeight.Bold,
                        fontSize = Typography.titleSmall.fontSize,
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                colors = ButtonDefaults.buttonColors(Color.Transparent),
                onClick = {
                    println("Hallo")
                    navController.navigate(route = Destination.Annotate)
                },
                modifier = Modifier
                    .background(
                        color = Color.Gray,
                        shape = RoundedCornerShape(20)
                    )
                    .fillMaxWidth()
            ) {
                Text(
                    "Start Training",
                    modifier = Modifier

                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }


    }
}