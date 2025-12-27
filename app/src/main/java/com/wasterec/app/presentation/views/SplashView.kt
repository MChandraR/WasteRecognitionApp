package com.wasterec.app.presentation.views

import android.graphics.Paint.Align
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.wasterec.app.R
import com.wasterec.app.presentation.components.GifLoader

@Composable
fun SplashView(){
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GifLoader(R.drawable.network)
    }
}

@Preview(showBackground = true)
@Composable
fun splash(){
    SplashView()
}