package com.wasterec.app.feature.navigation.view

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wasterec.app.feature.anotate.view.AnnotateView
import com.wasterec.app.feature.login.view.LoginView
import com.wasterec.app.feature.login.viewmodel.LoginViewModel
import com.wasterec.app.feature.splash.view.SplashView
import com.wasterec.app.feature.training.view.FinishTrainingView
import com.wasterec.app.feature.training.view.TrainingView
import com.wasterec.app.feature.main.MainView
import com.wasterec.app.manager.EfficientNetB0
import com.wasterec.app.model.Destination
import com.wasterec.app.model.TrainingModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationView(context: Context, navController : NavHostController, modifier: Modifier = Modifier) {
    val startDestination = Destination.Splash
    var selectedDestination by rememberSaveable { mutableIntStateOf(0) }
    var efficientNetB0: EfficientNetB0 = EfficientNetB0( context )
    val trainData = remember { mutableStateListOf<TrainingModel>() }
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable<Destination.Home> { MainView(navController) }
        composable<Destination.Info>{  }
        composable<Destination.Login>{ LoginView(navHostController = navController) }
        composable<Destination.Splash>{ SplashView() }
        composable<Destination.Training>{ TrainingView(context, efficientNetB0,  navController, trainData) }
        composable<Destination.Annotate>{ AnnotateView(context, navController, trainData) }
        composable<Destination.FinishTraining>{ FinishTrainingView(navController) }

    }

}



@Preview(showBackground = false)
@Composable
fun MainPreview(){
    MainView(rememberNavController())
}