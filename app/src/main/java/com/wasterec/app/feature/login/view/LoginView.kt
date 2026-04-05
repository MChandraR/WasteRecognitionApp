package com.wasterec.app.feature.login.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wasterec.app.R
import com.wasterec.app.feature.login.components.CustomTextInputField
import com.wasterec.app.feature.login.viewmodel.LoginViewModel
import com.wasterec.app.shared.components.Alert
import com.wasterec.app.ui.color.ColorAsset
import com.wasterec.app.ui.theme.Typography

@Composable
fun LoginView(loginViewModel: LoginViewModel){

    Box{
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding().fillMaxSize().background(Color.White).padding(20.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                "Selamat Datang",
                fontSize = Typography.displaySmall.fontSize,
                fontWeight = FontWeight.Bold,
                color = ColorAsset.primaryBlue
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "Silahkan login menggunakan akun klien",
                fontSize = Typography.titleMedium.fontSize
            )

            Spacer(modifier = Modifier.weight(1f))

            //Text field for username
            CustomTextInputField(
                text =  loginViewModel.username.value,
                onChange = {  loginViewModel.username.value = it},
                leadingResId = R.drawable.outline_person_24,
                leadingColor = ColorAsset.primaryBlue,
                backgroundColor = ColorAsset.alpha5,
                placeholder = {
                    Text("Masukkan username")
                }
            )

            Spacer(Modifier.height(20.dp))

            //Text field for password
            CustomTextInputField(
                text = loginViewModel.password.value,
                onChange = {  loginViewModel.password.value = it},
                leadingResId = R.drawable.outline_key_vertical_24,
                trailingResId = R.drawable.outline_eye_tracking_24,
                trailingColor = ColorAsset.primaryBlue,
                leadingColor = ColorAsset.primaryBlue,
                visualTransformation = if(loginViewModel.showPassword.value) VisualTransformation.None else PasswordVisualTransformation() ,
                onTrailingClick = {
                    loginViewModel.showPassword.value = !loginViewModel.showPassword.value
                },
                backgroundColor = ColorAsset.alpha5,
                placeholder = {
                    Text("Masukkan password")
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            //Button login action , execute login action in viewmodel
            Button(
                onClick = {
                    print("Hallo")
                    loginViewModel.login()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonColors(contentColor = Color.White, disabledContentColor = ColorAsset.lightGray, containerColor = ColorAsset.primaryBlue, disabledContainerColor = ColorAsset.lightGray)
            ){
                Text("Login",
                    modifier = Modifier.padding(5.dp, 10.dp),
                    fontSize = Typography.titleLarge.fontSize,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        //Alert to show information about login status
        Alert(
            title = if (loginViewModel.isSuccess.value) "Login Sukses" else "Login Gagal",
            message = if (loginViewModel.isSuccess.value) "Silahkan masuk ke aplikasi" else "Username atau password yang dimasukkan tidak sesuai !",
            icon = if (loginViewModel.isSuccess.value) R.drawable.shield else R.drawable.close ,
            showAlert = loginViewModel.showAlert.value,
        ) {
            loginViewModel.dismissAlert()
        }
    }

}


@Preview
@Composable
fun LoginViewPreview(){
    val password = remember {  mutableStateOf("") }
    Box{
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding().fillMaxSize().background(Color.White).padding(20.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                "Selamat Datang",
                fontSize = Typography.displaySmall.fontSize,
                fontWeight = FontWeight.Bold,
                color = ColorAsset.primaryBlue
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "Silahkan login menggunakan akun klien",
                fontSize = Typography.titleMedium.fontSize,
                color = ColorAsset.primaryBlue
            )

            Spacer(modifier = Modifier.weight(1f))

            //Text field for username
            CustomTextInputField(
                text = password.value,
                onChange = { password.value = it},
                leadingResId = R.drawable.outline_contacts_product_24,
                leadingColor = ColorAsset.primaryBlue,
                backgroundColor = ColorAsset.alpha5,
                maxLines = 1,
                placeholder = {
                    Text("Masukkan username")
                }
            )

            Spacer(Modifier.height(20.dp))

            //Text field for password
            CustomTextInputField(
                text = password.value,
                onChange = { password.value = it},
                leadingResId = R.drawable.outline_key_vertical_24,
                trailingResId = R.drawable.outline_eye_tracking_24,
                trailingColor = ColorAsset.primaryBlue,
                leadingColor = ColorAsset.primaryBlue,
                backgroundColor = ColorAsset.alpha5,
                maxLines = 1,
                placeholder = {
                    Text("Masukkan password")
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            //Button login action , execute login action in viewmodel
            Button(
                onClick = {
                    print("Hallo")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonColors(contentColor = Color.White, disabledContentColor = ColorAsset.lightGray, containerColor = ColorAsset.primaryBlue, disabledContainerColor = ColorAsset.lightGray)
            ){
                Text("Login",
                    modifier = Modifier.padding(5.dp, 10.dp),
                    fontSize = Typography.titleLarge.fontSize,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        //Alert to show information about login status
        Alert(
            title = "Login Sukses",
            message = "Ini contoh message",
            showAlert = true,
        ) {
        }
    }
}

