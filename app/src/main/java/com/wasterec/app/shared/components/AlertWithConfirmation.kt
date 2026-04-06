package com.wasterec.app.shared.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wasterec.app.R
import com.wasterec.app.ui.color.ColorAsset
import com.wasterec.app.ui.theme.Typography

@Composable
fun AlertWithConfirmation(
    title : String,
    message : String,
    icon : Int? = null,
    showAlert : Boolean,
    onConfirm  : ()->Unit = {},
    onCancel : ()->Unit = {}
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
                            Text(text = message, modifier = Modifier.padding(top = 10.dp), textAlign = TextAlign.Center, fontSize = Typography.titleSmall.fontSize)

                            Spacer(modifier = Modifier.height(10.dp))


                            if (icon != null) {
                                GifLoader(
                                    data = icon,
                                    modifier = Modifier.size(75.dp),
                                )
                            }

                            Row() {
                                Button(
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                    border = BorderStroke(2.dp, ColorAsset.primaryRed),
                                    onClick = {
                                        onCancel()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(.5f)
                                        .padding(top = 20.dp)
                                ) {
                                    Text("Cancel",
                                        color = ColorAsset.primaryRed)
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Button(
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = ColorAsset.primaryBlue),
                                    onClick = {
                                        onConfirm()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(.5f)
                                        .padding(top = 20.dp)
                                ) {
                                    Text("Ok")
                                }
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
fun PreviewAlertWithConfirmationComponents(){
    var showAlert by remember { mutableStateOf(true) }
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ){
        AlertWithConfirmation(
            title="Alert",
            message="This is a message for alert !",
            icon = R.drawable.trash_waste,
            showAlert = showAlert,
            onConfirm = {showAlert = false},
            onCancel = {}
        )
    }

}