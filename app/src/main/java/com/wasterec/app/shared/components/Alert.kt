package com.wasterec.app.shared.components

import android.content.res.Resources.Theme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wasterec.app.ui.color.ColorAsset
import com.wasterec.app.ui.theme.Typography

@Composable
fun Alert(
    title : String,
    message : String,
    showAlert : Boolean,
    onDismiss  : ()->Unit
){
    when {
        showAlert -> {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .background(ColorAsset.alertMainBg)
            ){
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(corner = CornerSize(10.dp))
                        )
                        .padding(30.dp, 20.dp)
                ) {
                    Row {
                        Spacer(modifier = Modifier.weight(.5f))

                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = title, fontSize = Typography.titleMedium.fontSize, fontWeight = FontWeight.Bold)
                            Text(text = message, modifier = Modifier.padding(top = 10.dp))
                            Button(
                                onClick = {
                                    onDismiss()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 20.dp)
                            ) {
                                Text("Ok")
                            }
                        }

                        Spacer(modifier = Modifier.weight(.5f))
                    }

                }
            }

        }
    }
}

@Preview
@Composable
fun Preview(){
    var showAlert by remember { mutableStateOf(true) }
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ){
        Alert(
            title="Alert",
            message="This is a message for alert !",
            showAlert = showAlert,
            onDismiss = {showAlert = false}
        )
    }

}