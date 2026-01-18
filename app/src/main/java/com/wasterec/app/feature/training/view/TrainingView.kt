package com.wasterec.app.feature.training.view

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.wasterec.app.feature.training.viewmodel.TrainingViewModel
import com.wasterec.app.model.Destination
import com.wasterec.app.shared.components.MyButton
import com.wasterec.app.ui.color.ColorAsset
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
            Text("Performa model lokal",
                fontSize = Typography.titleMedium.fontSize,
                fontWeight = FontWeight.Bold
            )
            Column (
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(10.dp)
                    .background(
                    color = ColorAsset.lightGray,
                    shape =  RoundedCornerShape(10.dp)
                )
            ){
                Text(
                    "20%",
                    fontSize = Typography.displayLarge.fontSize * 1.2,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 50.dp, end = 50.dp, top = 20.dp)
                )
                Text("Total Image",
                    modifier = Modifier.padding(bottom = 20.dp))
            }

        }

        Spacer(Modifier.weight(.3f))

        Column {
            Text(
                "Detail Dataset",
                fontSize = Typography.titleMedium.fontSize,
                fontWeight = FontWeight.Bold
            )

            ConstraintLayout(
                modifier = Modifier
                    .background(
                        color = ColorAsset.lightGray,
                        shape = RoundedCornerShape(10)
                    )
                    .fillMaxWidth()
            ) {
                val (count, label ) = createRefs()

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(25.dp)
                        .constrainAs(count){
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(label.start)
                        }
                ) {
                    Text(
                        "${trainingViewModel?.importImageViewModel?.imageDatasetList?.size}",
                        fontSize = Typography.displayLarge.fontSize * 1.2,
                        fontWeight = FontWeight.Bold,
                    )
                    Text("Total Image")
                }


                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier
                        .constrainAs(label){
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            end.linkTo(parent.end, 10.dp)
                        }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        for(i in 0 ..< 3){
                            Box(modifier = Modifier.border(1.dp, Color.Black, RoundedCornerShape(5.dp))){
                                Text("Plastik : 20", modifier = Modifier.padding(10.dp,5.dp))
                            }
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        for(i in 0 ..< 3){
                            Box(modifier = Modifier.border(1.dp, Color.Black, RoundedCornerShape(5.dp))){
                                Text("Plastik : 20", modifier = Modifier.padding(10.dp,5.dp))
                            }
                        }
                    }

                }
            }
        }

        if(trainingViewModel != null) {
            CartesianChartHost(
                modifier = Modifier.padding(top = 50.dp),
                chart = rememberCartesianChart(
                    rememberLineCartesianLayer(),
                    startAxis = VerticalAxis.rememberStart(),
                    bottomAxis = HorizontalAxis.rememberBottom(),
                ),
                modelProducer = trainingViewModel.modelProducer.value,
            )
        }

        Spacer(Modifier.weight(1f))

//        GifLoader(R.drawable.network)
        Text(
            "Loss ${trainingViewModel?.currentLoss?.value}" + ".".repeat(loadIdx),
            fontSize = Typography.titleLarge.fontSize
        )
        


        Spacer(Modifier.weight(1f))

        Text(
            "Iterasi ${trainingViewModel?.currentEpoch?.value?:0}/${trainingViewModel?.modelConfig?.epoch}",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.End,
            fontSize = Typography.bodyLarge.fontSize
        )

        LinearProgressIndicator(
            progress = { ((trainingViewModel?.currentEpoch?.value?: 0).toFloat() / ((trainingViewModel?.modelConfig?.epoch?:1).toFloat())).toFloat() },
            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp).height(10.dp)
        )

        MyButton(
            onClick = {
                trainingViewModel?.navHostController?.navigate(route = Destination.FinishTraining)
            },
            modifier = Modifier
                .fillMaxWidth()
        ) { 
            Text("Selesai",
                modifier = Modifier.padding(10.dp))
        }

        LaunchedEffect(Unit){
            trainingViewModel?.reInit()
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

