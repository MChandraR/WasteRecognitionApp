package com.wasterec.app.shared.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MyButton(
    onClick : () -> Unit,
    modifier:Modifier = Modifier,
    content : @Composable () -> Unit,
    ){
    Button(
        onClick = {
            onClick()
        },
        shape = RoundedCornerShape(10.dp),
        colors = ButtonColors(containerColor = Color.LightGray, contentColor = Color.Black, disabledContainerColor = Color.LightGray, disabledContentColor = Color.Black),
        modifier = modifier
    ) {
        content()
    }
}