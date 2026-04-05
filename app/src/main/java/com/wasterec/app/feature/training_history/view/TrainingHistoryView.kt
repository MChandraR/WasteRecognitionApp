package com.wasterec.app.feature.training_history.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wasterec.app.feature.training_history.components.TrainingHistoryCard
import com.wasterec.app.feature.training_history.viewmodel.TrainingHistoryViewModel
import com.wasterec.app.ui.color.ColorAsset
import com.wasterec.app.ui.theme.Typography

@Composable
fun TrainingHistoryView(
    trainingHistoryViewModel : TrainingHistoryViewModel? = null
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().padding(20.dp)
    ) {
        Spacer(modifier = Modifier.weight(.1f))

        Text("Training History",
            fontSize = Typography.displaySmall.fontSize,
            fontWeight = FontWeight.Bold,
            color = ColorAsset.primaryBlue
        )

        Spacer(modifier = Modifier.weight(.1f))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(5){
                TrainingHistoryCard(1, "s890df", "0.1232", "2023-01-01 at 09:00")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TrainingHistoryViewPreview(){
    TrainingHistoryView()
}