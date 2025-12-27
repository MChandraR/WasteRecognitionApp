package com.wasterec.app.presentation.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.wasterec.app.R
import com.wasterec.app.model.Destination
import com.wasterec.app.presentation.components.MyButton
import com.wasterec.app.ui.theme.Typography

@Composable
fun FinishTrainingView(
    navController: NavHostController
){
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ){
        Spacer(Modifier.weight(1f))




        Text(
            "Training Finished",
            fontSize = Typography.titleLarge.fontSize,
            fontWeight = FontWeight.Bold
        )


        Image(
            painter = painterResource(R.drawable.cloud),
            "Cloud"
        )

        Spacer(Modifier.weight(1f))


        MyButton(
            onClick = {
                navController.navigate(Destination.Home) {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }

            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 20.dp)
        ) {
            Text("Back")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun Finish(){
    FinishTrainingView(rememberNavController())
}