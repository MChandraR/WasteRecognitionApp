package com.wasterec.app.feature.login.view

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.wasterec.app.feature.login.factory.LoginViewModelFactory
import com.wasterec.app.feature.login.viewmodel.LoginViewModel
import com.wasterec.app.shared.components.Alert

@Composable
fun LoginView(navHostController: NavHostController){
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val loginViewModel:LoginViewModel = viewModel(
        factory = LoginViewModelFactory(
            app = application,
            context = context,
            navHostController = navHostController
        )
    )

    Box{
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding().fillMaxSize().background(Color.White).padding(20.dp)
        ) {
            Text("Username",
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
            //Text field for username
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
            //Text field for password
            TextField(
                value = loginViewModel.password.value,
                onValueChange = { loginViewModel.password.value = it },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1,
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(60.dp))

            //Button login action , execute login action in viewmodel
            Button(
                onClick = {
                    print("Hallo")
                    loginViewModel.login()
                },
                modifier = Modifier.fillMaxWidth()
            ){
                Text("Login",
                    modifier = Modifier.padding(5.dp, 10.dp)
                )
            }
        }

        //Alert to show information about login status
        Alert(
            title = "Sukses",
            message = "Berhasil login",
            showAlert = loginViewModel.showAlert.value,
        ) {
            loginViewModel.dismissAlert()
        }
    }

}


