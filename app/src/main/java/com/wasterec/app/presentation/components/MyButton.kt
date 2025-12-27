package com.wasterec.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

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
        colors = ButtonDefaults.buttonColors(Color.Transparent),
        modifier = modifier
            .background(
                color = Color.Gray,
                shape = RoundedCornerShape(10)
            )


    ) {
        content()
    }
}