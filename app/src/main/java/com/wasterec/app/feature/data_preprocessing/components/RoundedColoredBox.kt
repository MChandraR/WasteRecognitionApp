package com.wasterec.app.feature.data_preprocessing.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wasterec.app.ui.theme.Typography

@Composable
fun RoundedColoredBox(
    leadingText : String,
    trailingText : String,
    color : Color = Color.Blue,
    cornerSize : Int = 10,
    modifier : Modifier = Modifier
    )
    {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
            .clip(RoundedCornerShape(cornerSize.dp))
            .border(1.dp, Color(color.red, color.green, color.blue, alpha = 0.5f), RoundedCornerShape(cornerSize.dp))
            .padding(10.dp, 10.dp, bottom = 10.dp, end = 10.dp)
    ) {
        Text(leadingText,
            color = color,
            fontSize = Typography.displayLarge.fontSize,
            fontWeight = FontWeight.Bold
        )
        Text(trailingText,
            color = color,
            fontSize = Typography.bodyLarge.fontSize,
            fontWeight = FontWeight.Bold
        )
    }
}


@Preview(showBackground = true)
@Composable
fun RoundedBoxPreview(){

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().padding(20.dp) ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ){
            RoundedColoredBox("20", "Rotated", modifier = Modifier.weight(.5f))
            RoundedColoredBox("20", "Flipped Vertically", modifier = Modifier.weight(.5f))
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ){
            RoundedColoredBox("20", "Rotated", modifier = Modifier.weight(.5f))
            RoundedColoredBox("20", "Rotated", modifier = Modifier.weight(.5f))
        }
    }
}