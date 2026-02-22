package com.wasterec.app.shared.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SecondaryButton(
    onClick : () -> Unit,
    modifier:Modifier = Modifier,
    content : @Composable () -> Unit,
    ){
    Button(
        onClick = {
            onClick()
        },
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp , Color.DarkGray),
        colors = ButtonColors(containerColor = Color.Transparent, contentColor = Color.Black, disabledContainerColor = Color.LightGray, disabledContentColor = Color.Black),
        modifier = modifier
    ) {
        content()
    }
}