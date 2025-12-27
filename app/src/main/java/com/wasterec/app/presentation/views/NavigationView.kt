package com.wasterec.app.presentation.views

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wasterec.app.manager.EfficientNetB0
import com.wasterec.app.model.Destination
import com.wasterec.app.model.TrainingModel

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