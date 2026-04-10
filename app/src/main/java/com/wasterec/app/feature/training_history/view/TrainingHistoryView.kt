package com.wasterec.app.feature.training_history.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wasterec.app.R
import com.wasterec.app.feature.training_history.components.TrainingHistoryCard
import com.wasterec.app.feature.training_history.viewmodel.TrainingHistoryViewModel
import com.wasterec.app.model.Destination
import com.wasterec.app.ui.color.ColorAsset
import com.wasterec.app.ui.theme.Typography
import kotlinx.coroutines.Dispatchers
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TrainingHistoryView(
    trainingHistoryViewModel : TrainingHistoryViewModel? = null
){
    LaunchedEffect(Dispatchers.IO) {
        trainingHistoryViewModel?.fetchTrainingData()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Spacer(modifier = Modifier.weight(.1f))

        Text("Training History",
            fontSize = Typography.displaySmall.fontSize,
            fontWeight = FontWeight.Bold,
            color = ColorAsset.primaryBlue,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 55.dp)
        )

//        HorizontalDivider(modifier = Modifier.height(2.dp).padding(vertical = 10.dp))

        Spacer(modifier = Modifier.height(20.dp))

        if((trainingHistoryViewModel?.trainingHistoryData?.size ?: 0) > 0){
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f).scrollable(
                    state = rememberScrollState(),
                    orientation = Orientation.Vertical
                ).padding(bottom = 100.dp)
            ) {
                items(trainingHistoryViewModel?.trainingHistoryData?.size ?: 0){ idx ->
                    val trainingData = trainingHistoryViewModel?.trainingHistoryData?.get(idx) ?: return@items
                    TrainingHistoryCard(idx + 1, trainingData.session_id, "${trainingData.average_loss}",
                        trainingData.created_at.slice(IntRange(0,18)),
                        modifier = Modifier.clickable(true){
                            trainingHistoryViewModel.selectedTrainingData?.value = trainingData
                            trainingHistoryViewModel.navHostController.navigate(Destination.TrainingHistoryDetail)
                        }
                    )

                }
            }
        }else{
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.weight(1f))

                Image(
                    painter = painterResource(R.drawable.no_data),
                    "",
                    alpha = .5f,
                    colorFilter = ColorFilter.tint(ColorAsset.tertiaryBlue),
                    modifier = Modifier.height(100.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text("Belum ada data pelatihan !",
                    fontSize = Typography.titleMedium.fontSize,
                    color = ColorAsset.tertiaryBlue
                    )

                Spacer(modifier = Modifier.weight(1f))
            }

        }

    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun TrainingHistoryViewPreview(){
    TrainingHistoryView()
}