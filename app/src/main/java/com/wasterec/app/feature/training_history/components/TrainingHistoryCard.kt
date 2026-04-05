package com.wasterec.app.feature.training_history.components

import android.media.Image
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wasterec.app.R
import com.wasterec.app.feature.training_history.view.TrainingHistoryView
import com.wasterec.app.ui.color.ColorAsset
import com.wasterec.app.ui.theme.Typography

@Composable
fun TrainingHistoryCard(number: Int, id : String, loss : String, date : String){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, ColorAsset.shadeBlue, RoundedCornerShape(10.dp))
                .padding(start = 20.dp, 10.dp, 20.dp, 10.dp)
        ) {
            Text("${number}",
                fontWeight = FontWeight.Bold,
                fontSize = Typography.displayMedium.fontSize,
                color = ColorAsset.primaryBlue,
                modifier = Modifier.padding(start = 10.dp, end= 30.dp)
                )

            Column(modifier = Modifier.weight(1f)) {
                Text("ID : ${id}",
                    fontSize = Typography.bodyMedium.fontSize,
                    color= ColorAsset.tertiaryBlue
                )
                Text("Loss : ${loss}",
                    fontSize = Typography.titleMedium.fontSize,
                    fontWeight = FontWeight.Bold,
                    color= ColorAsset.primaryBlue
                )
                Text("${date}",
                    fontSize = Typography.bodyMedium.fontSize,
                    color= ColorAsset.tertiaryBlue
                )
            }

            Image(
                painter = painterResource(R.drawable.outline_chevron_right_24),
                "",
                colorFilter = ColorFilter.tint(ColorAsset.primaryBlue),
            )
    }
}

@Preview(showBackground = true)
@Composable
fun TrainingHistoryCardPreview(){
//    Column(modifier = Modifier.fillMaxSize()) {
//        TrainingHistoryCard(1, "s890df", "0.1232", "2023-01-01 at 09:00")
//
//    }
    TrainingHistoryView()
}