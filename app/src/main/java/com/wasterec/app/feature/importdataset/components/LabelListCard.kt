package com.wasterec.app.feature.importdataset.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wasterec.app.R
import com.wasterec.app.ui.color.ColorAsset
import com.wasterec.app.ui.theme.Typography

@Composable
fun LabelListCard(
    title : String,
    content : String,
    leadingIcon : Int,
    titleColor : Color = Color.Black,
    modifier: Modifier = Modifier.padding(10.dp)
){
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .border(2.dp, ColorAsset.lightGray, RoundedCornerShape(10.dp))
            .padding(5.dp)
    ) {
        Image(painterResource(leadingIcon), "icon",modifier= Modifier.padding(5.dp,10.dp,0.dp,10.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(title, fontSize = Typography.titleSmall.fontSize, fontWeight = FontWeight.Bold, color = titleColor)
            Text(content, fontSize = Typography.bodyLarge.fontSize)
        }
        Image(
            painterResource(R.drawable.edit_icon), "icon",
            modifier = Modifier.padding(15.dp)
        )

    }
}

@Preview(showBackground = true)
@Composable
fun LabelListCardPreview(){
    LabelListCard("Plastik", "10/1", R.drawable.trash_waste,  modifier = Modifier.height(60.dp))
}