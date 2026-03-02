package com.wasterec.app.feature.navigation.view

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.wasterec.app.feature.anotate.view.AnnotateView
import com.wasterec.app.feature.anotate.viewmodel.AnnotateViewModel
import com.wasterec.app.feature.home.viewmodel.HomeViewModel
import com.wasterec.app.feature.importdataset.view.ImportDatasetView
import com.wasterec.app.feature.importdataset.viewmodel.ImportDatasetViewModel
import com.wasterec.app.feature.importimage.view.ImportImageView
import com.wasterec.app.feature.importimage.viewmodel.ImportImageViewModel
import com.wasterec.app.feature.login.view.LoginView
import com.wasterec.app.feature.login.viewmodel.LoginViewModel
import com.wasterec.app.feature.main.MainView
import com.wasterec.app.feature.modelload.view.ModelLoadView
import com.wasterec.app.feature.modelload.viewmodel.ModelLoadViewModel
import com.wasterec.app.feature.splash.view.SplashView
import com.wasterec.app.feature.training.view.FinishTrainingView
import com.wasterec.app.feature.training.view.TrainingView
import com.wasterec.app.feature.training.viewmodel.TrainingViewModel
import com.wasterec.app.model.Destination

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationView(
    context: Context,
    navController : NavHostController,
    loginViewModel : LoginViewModel,
    homeViewModel : HomeViewModel,
    importDatasetViewModel: ImportDatasetViewModel,
    annotateViewModel: AnnotateViewModel,
    importImageViewModel: ImportImageViewModel,
    trainingViewModel: TrainingViewModel,
    modelLoadViewModel: ModelLoadViewModel,
) {
    val startDestination = Destination.Splash
    var selectedDestination by rememberSaveable { mutableIntStateOf(0) }

    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable<Destination.Home> { MainView(homeViewModel) }
        composable<Destination.Info>{  }
        composable<Destination.Login>{ LoginView(loginViewModel) }
        composable<Destination.Splash>{ SplashView() }
        composable<Destination.Import>{ ImportImageView(navController, importImageViewModel) }
        composable<Destination.ImportDataset>{ ImportDatasetView(importDatasetViewModel) }
        composable<Destination.Training>{ TrainingView(trainingViewModel) }
        composable<Destination.Annotate>{ AnnotateView(annotateViewModel, importImageViewModel) }
        composable<Destination.FinishTraining>{ FinishTrainingView(navController) }
        composable<Destination.WeightLoading>{ ModelLoadView(modelLoadViewModel) }

    }

}



//@Preview(showBackground = false)
//@Composable
//fun MainPreview(){
//    MainView(rememberNavController())
//}