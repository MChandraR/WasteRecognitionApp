package com.wasterec.app.feature.navigation.view

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wasterec.app.feature.anotate.view.AnnotateView
import com.wasterec.app.feature.anotate.viewmodel.AnotateViewModel
import com.wasterec.app.feature.home.viewmodel.HomeViewModel
import com.wasterec.app.feature.importdataset.view.ImportDatasetView
import com.wasterec.app.feature.importdataset.viewmodel.ImportDatasetViewModel
import com.wasterec.app.feature.importimage.viewmodel.ImportImageViewModel
import com.wasterec.app.feature.login.view.LoginView
import com.wasterec.app.feature.splash.view.SplashView
import com.wasterec.app.feature.training.view.FinishTrainingView
import com.wasterec.app.feature.training.view.TrainingView
import com.wasterec.app.feature.main.MainView
import com.wasterec.app.manager.EfficientNetB0
import com.wasterec.app.model.Destination
import com.wasterec.app.model.TrainingModel
import com.wasterec.app.feature.importimage.view.ImportImageView
import com.wasterec.app.feature.login.viewmodel.LoginViewModel
import com.wasterec.app.feature.modelload.view.ModelLoadView
import com.wasterec.app.feature.modelload.viewmodel.ModelLoadViewModel
import com.wasterec.app.feature.training.viewmodel.TrainingViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationView(
    context: Context,
    navController : NavHostController,
    loginViewModel : LoginViewModel,
    homeViewModel : HomeViewModel,
    importDatasetViewModel: ImportDatasetViewModel,
    anotateViewModel: AnotateViewModel,
    importImageViewModel: ImportImageViewModel,
    trainingViewModel: TrainingViewModel,
    modelLoadViewModel: ModelLoadViewModel,
) {
    val startDestination = Destination.Splash
    var selectedDestination by rememberSaveable { mutableIntStateOf(0) }
    var efficientNetB0: EfficientNetB0 = EfficientNetB0( context )
    val trainData = remember { mutableStateListOf<TrainingModel>() }
    val selectedLabelIndex : MutableState<Int> = remember {mutableStateOf(0)}

    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable<Destination.Home> { MainView(homeViewModel) }
        composable<Destination.Info>{  }
        composable<Destination.Login>{ LoginView(loginViewModel) }
        composable<Destination.Splash>{ SplashView() }
        composable<Destination.Import>{ ImportImageView(navController, importImageViewModel, selectedLabelIndex) }
        composable<Destination.ImportDataset>{ ImportDatasetView(importDatasetViewModel, selectedLabelIndex) }
        composable<Destination.Training>{ TrainingView(trainingViewModel) }
        composable<Destination.Annotate>{ AnnotateView(anotateViewModel, importImageViewModel) }
        composable<Destination.FinishTraining>{ FinishTrainingView(navController) }
        composable<Destination.WeightLoading>{ ModelLoadView(modelLoadViewModel) }

    }

}



//@Preview(showBackground = false)
//@Composable
//fun MainPreview(){
//    MainView(rememberNavController())
//}