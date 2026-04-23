package com.wasterec.app.feature.training_history_detail.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
import com.wasterec.app.feature.importdataset.data.datasetClassList
import com.wasterec.app.feature.training.ui.trainingColorList
import com.wasterec.app.feature.training_history_detail.viewmodel.TrainingHistoryDetailViewModel
import com.wasterec.app.shared.components.MyButton
import com.wasterec.app.shared.components.SecondaryButton
import com.wasterec.app.ui.color.ColorAsset
import com.wasterec.app.ui.theme.Typography
import kotlinx.coroutines.Dispatchers

@Composable
fun TrainingHistoryDetailView(
    trainingHistoryDetailViewModel : TrainingHistoryDetailViewModel? = null
){

    LaunchedEffect(Dispatchers.IO) {
        trainingHistoryDetailViewModel?.updateLossChartData()
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Spacer(Modifier.weight(1f))

            Text(
                "Training History",
                fontSize = Typography.displaySmall.fontSize,
                fontWeight = FontWeight.Bold,
                color = ColorAsset.primaryBlue,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Text("ID : ${trainingHistoryDetailViewModel?.selectedTrainingHistory?.value?.session_id ?: "-"}",
                fontSize = Typography.titleSmall.fontSize,
                fontWeight = FontWeight.Bold,
                color = ColorAsset.tertiaryBlue
            )

            Spacer(modifier = Modifier.weight(1f))


            Column (verticalArrangement = Arrangement.spacedBy(10.dp)){
//                Text(
//                    "Detail Dataset",
//                    fontSize = Typography.titleSmall.fontSize,
//                    fontWeight = FontWeight.Bold,
//                    color = ColorAsset.darkerBlue
//                )

                ConstraintLayout(
                    modifier = Modifier
                        .background(
                            color = Color.Transparent,
                            shape = RoundedCornerShape(10)
                        )
                        .border(1.dp, ColorAsset.primaryBlue25, RoundedCornerShape(10))
                        .fillMaxWidth()
                ) {
                    val (count, label ) = createRefs()

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .padding(25.dp)
                            .constrainAs(count) {
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                                start.linkTo(parent.start)
                                end.linkTo(label.start)
                            }
                    ) {
                        Text(
                            "${trainingHistoryDetailViewModel?.selectedTrainingHistory?.value?.label_count?.sum() ?: 0}",
                            fontSize = Typography.displayMedium.fontSize ,
                            fontWeight = FontWeight.Bold,
                            color = ColorAsset.primaryBlue
                        )
                        Text("Total Image",
                            fontWeight = FontWeight.Bold,
                            color = ColorAsset.primaryBlue)
                    }


                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        modifier = Modifier
                            .constrainAs(label){
                                top.linkTo(parent.top,15.dp)
                                bottom.linkTo(parent.bottom, 15.dp)
                                end.linkTo(parent.end, 10.dp)
                            }
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                            for(i in 0 ..< 3){
                                Box(modifier = Modifier.border(1.dp,
                                    trainingColorList[i], RoundedCornerShape(5.dp))){
                                    Text(
                                        "${(trainingHistoryDetailViewModel?.label?.get(i) ?: datasetClassList[i].className).padEnd(7 + (kotlin.math.max(0,(i - 2)) * 2))} : ${trainingHistoryDetailViewModel?.selectedTrainingHistory?.value?.label_count?.get(i) ?: 0}", modifier = Modifier.padding(10.dp,8.dp), fontWeight = FontWeight.Bold, color = trainingColorList[i],
                                        fontSize = Typography.bodySmall.fontSize)
                                }
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                            for(i in 3 ..< 6){
                                Box(modifier = Modifier.border(1.dp,
                                    trainingColorList[i], RoundedCornerShape(5.dp))){
                                    Text("${(trainingHistoryDetailViewModel?.label?.get(i) ?: datasetClassList[i].className ).padEnd(7 - kotlin.math.max(0,(i - 4)))} : ${
                                        trainingHistoryDetailViewModel?.selectedTrainingHistory?.value?.label_count?.get(
                                            i
                                        ) ?: 0
                                    }",
                                        fontSize = Typography.bodySmall.fontSize,
                                        modifier = Modifier.padding(10.dp,8.dp), fontWeight = FontWeight.Bold, color = trainingColorList[i]
                                    )
                                }
                            }
                        }

                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))

            Row (
//                verticalArrangement = Arrangement.Center,
//                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .fillMaxWidth()
//                    .border(2.dp, ColorAsset.shadeBlue, RoundedCornerShape(10.dp))
            ){
                Text("Average Loss",
                    fontSize = Typography.titleSmall.fontSize,
                    fontWeight = FontWeight.Bold,
                    color = ColorAsset.primaryBlue,
                    modifier = Modifier
                )
                Text(
                    "%.3f".format(trainingHistoryDetailViewModel?.selectedTrainingHistory?.value?.last_loss ?: 0f),
                    fontSize = Typography.titleLarge.fontSize ,
                    fontWeight = FontWeight.Bold,
                    color = ColorAsset.primaryBlue,
//                    modifier = Modifier.padding(start = 30.dp, end = 30.dp, top = 20.dp)
                )

            }

            if(trainingHistoryDetailViewModel != null) {
                CartesianChartHost(
                    modifier = Modifier.padding(top = 10.dp),
                    chart = rememberCartesianChart(
                        rememberLineCartesianLayer(),
                        startAxis = VerticalAxis.rememberStart(),
                        bottomAxis = HorizontalAxis.rememberBottom(),
                    ),
                    modelProducer = trainingHistoryDetailViewModel.modelProducer.value,
                )
            }

            Spacer(Modifier.weight(1f))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SecondaryButton(
                    onClick = {
                        trainingHistoryDetailViewModel?.deleteTrainingData()
                    },
                    borderColor = ColorAsset.primaryRed,
                    borderSize = 2,
                    contentColor = ColorAsset.primaryRed,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Hapus",
                        fontSize = Typography.titleSmall.fontSize,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                MyButton(
                    onClick = {
                        trainingHistoryDetailViewModel?.navHostController?.popBackStack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        "Kembali",
                        fontSize = Typography.titleSmall.fontSize,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TrainingHistoryDetailViewPreview(){
    TrainingHistoryDetailView()
}