package com.wasterec.app.feature.training.view

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.wasterec.app.R
import com.wasterec.app.feature.importimage.viewmodel.ImportImageViewModel
import com.wasterec.app.feature.training.viewmodel.TrainingViewModel
import com.wasterec.app.feature.training.viewmodelfactory.TrainingViewModelFactory
import com.wasterec.app.manager.EfficientNetB0
import com.wasterec.app.model.Destination
import com.wasterec.app.model.ModelConiguration
import com.wasterec.app.model.TrainingModel
import com.wasterec.app.shared.components.GifLoader
import com.wasterec.app.shared.components.MyButton
import com.wasterec.app.ui.theme.Typography
import kotlinx.coroutines.delay
import java.lang.Integer.max

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TrainingView(
    trainingViewModel: TrainingViewModel?
){
    var loadIdx by remember{ mutableIntStateOf(1) }

    LaunchedEffect(Unit) {
        while (true) {
            loadIdx++
            loadIdx = max(1, loadIdx % 4)
            delay(1000)
        }
    }




    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Spacer(Modifier.weight(1f))

        Text(
            "Training",
            fontSize = Typography.displayLarge.fontSize,
            fontWeight = FontWeight.Bold
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(30.dp)
        ) {
            Text("Performa model lokal")
            Column (
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(20.dp)
                    .background(
                    color = Color.LightGray,
                    shape =  RoundedCornerShape(10.dp)
                )
            ){
                Text(
                    "20%",
                    fontSize = Typography.displayLarge.fontSize,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(50.dp,20.dp)
                )
                Text("Total Image")
            }

        }

        Spacer(Modifier.weight(1f))

        ConstraintLayout(
            modifier = Modifier
                .background(
                    color = Color.LightGray,
                    shape = RoundedCornerShape(10)
                )
                .fillMaxWidth()
                .padding(all = 10.dp)
        ) {
            val (count, label ) = createRefs()

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(30.dp)
                    .constrainAs(count){
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    }
            ) {
                Text(
                    "20",
                    fontSize = Typography.displayLarge.fontSize,
                    fontWeight = FontWeight.Bold,
                )
                Text("Total Image")
            }


            Column(
                modifier = Modifier
                    .constrainAs(label){
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                        start.linkTo(count.end)
                    }
            ) {
                Text("Total Correct : ")
                Text(
                    "20",
                    fontSize = Typography.titleLarge.fontSize,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(10.dp))

                Text("Total Correct : ")
                Text(
                    "20",
                    fontSize = Typography.titleLarge.fontSize,
                    fontWeight = FontWeight.Bold
                )

            }
        }

        Spacer(Modifier.weight(1f))

        GifLoader(R.drawable.network)
        Text(
            "Training" + ".".repeat(loadIdx),
            fontSize = Typography.titleLarge.fontSize
        )

        Spacer(Modifier.weight(1f))

        MyButton(
            onClick = {
                trainingViewModel?.navHostController?.navigate(route = Destination.FinishTraining)
            },
            modifier = Modifier
                .fillMaxWidth()
        ) { 
            Text("Done")
        }

        LaunchedEffect(Unit){
            trainingViewModel?.startLocalTraining()
            Toast.makeText(trainingViewModel?.context, "Training dengan total : ${trainingViewModel?.importImageViewModel?.imageDatasetList?.count()}", Toast.LENGTH_SHORT).show()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun TrainingViewPreview(){
    TrainingView(null)
}

