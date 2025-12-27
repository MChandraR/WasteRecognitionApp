package com.wasterec.app.presentation.views

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wasterec.app.presentation.viewmodel.LoginViewModel

@Composable
fun LoginView(context: Context){
    val loginViewModel = remember { LoginViewModel(context)}

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding()
            .fillMaxSize()
            .background(
                Color.White
            )
            .padding(20.dp)
    ) {
        Text("Username",
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = loginViewModel.username.value,
            onValueChange = { loginViewModel.username.value = it },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1
        )

        Spacer(Modifier.height(50.dp))

        Text("Password",
                modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = loginViewModel.password.value,
            onValueChange = { loginViewModel.password.value = it },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1,
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(60.dp))

        Button(
            onClick = {
                print("Hallo")
                loginViewModel.login()
            },
            modifier = Modifier
                .fillMaxWidth()
        ){
            Text("Login",
                    modifier = Modifier
                        .padding(5.dp, 10.dp)
            )
        }
    }
}


