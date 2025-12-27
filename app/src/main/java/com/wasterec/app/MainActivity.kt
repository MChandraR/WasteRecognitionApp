package com.wasterec.app

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.wasterec.app.feature.login.view.LoginView
import com.wasterec.app.feature.main.DebugView
import com.wasterec.app.feature.navigation.view.NavigationView
import com.wasterec.app.model.Destination

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val handler = Handler(Looper.getMainLooper())

        setContent {
            val navController = rememberNavController()
            //DebugView(this)
            NavigationView(
                this,
                navController
            )

            handler.postDelayed({
                navController.navigate(Destination.Home) {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }, 3000)
        }


    }


}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MainActivity ()
}