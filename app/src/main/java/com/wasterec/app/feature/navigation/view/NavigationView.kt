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
import com.wasterec.app.feature.classify.view.ClassifyView
import com.wasterec.app.feature.classify.viewmodel.ClassifyViewModel
import com.wasterec.app.feature.data_preprocessing.view.DataProcessingView
import com.wasterec.app.feature.data_preprocessing.viewmodel.DataProcessingViewModel
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
import com.wasterec.app.feature.neural_search.NSView
import com.wasterec.app.feature.neural_search.NSViewModel
import com.wasterec.app.feature.splash.view.SplashView
import com.wasterec.app.feature.training.view.FinishTrainingView
import com.wasterec.app.feature.training.view.TrainingView
import com.wasterec.app.feature.training.viewmodel.FinishTrainingViewModel
import com.wasterec.app.feature.training.viewmodel.TrainingViewModel
import com.wasterec.app.feature.training_history.viewmodel.TrainingHistoryViewModel
import com.wasterec.app.feature.training_history_detail.view.TrainingHistoryDetailView
import com.wasterec.app.feature.training_history_detail.viewmodel.TrainingHistoryDetailViewModel
import com.wasterec.app.model.Destination

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationView(
    context: Context,
    navController : NavHostController,
    loginViewModel : LoginViewModel,
    homeViewModel : HomeViewModel,
    importDatasetViewModel: ImportDatasetViewModel,
    dataestProcessingViewModel: DataProcessingViewModel,
    annotateViewModel: AnnotateViewModel,
    importImageViewModel: ImportImageViewModel,
    trainingViewModel: TrainingViewModel,
    modelLoadViewModel: ModelLoadViewModel,
    finishTrainingViewModel: FinishTrainingViewModel,
    trainingHistoryViewModel: TrainingHistoryViewModel,
    trainingHistoryDetailViewModel: TrainingHistoryDetailViewModel,
    nsViewModel : NSViewModel,
    classifyViewModel : ClassifyViewModel
) {
    val startDestination = Destination.Splash
    var selectedDestination by rememberSaveable { mutableIntStateOf(0) }

    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable<Destination.Home> { MainView(homeViewModel, trainingHistoryViewModel) }
        composable<Destination.Info>{  }
        composable<Destination.Preprocess>{ DataProcessingView(dataestProcessingViewModel) }
        composable<Destination.Login>{ LoginView(loginViewModel) }
        composable<Destination.Splash>{ SplashView() }
        composable<Destination.Import>{ ImportImageView(navController, importImageViewModel) }
        composable<Destination.ImportDataset>{ ImportDatasetView(importDatasetViewModel) }
        composable<Destination.Training>{ TrainingView(trainingViewModel) }
        composable<Destination.Annotate>{ AnnotateView(annotateViewModel) }
        composable<Destination.FinishTraining>{ FinishTrainingView(finishTrainingViewModel) }
        composable<Destination.WeightLoading>{ ModelLoadView(modelLoadViewModel) }
        composable<Destination.WeightLoadingForClassification>{ ModelLoadView(modelLoadViewModel) }
        composable<Destination.TrainingHistoryDetail>{ TrainingHistoryDetailView(trainingHistoryDetailViewModel) }
        composable<Destination.Testing>{ NSView(nsViewModel) }
        composable<Destination.Classify>{ ClassifyView(classifyViewModel) }
    }

}



//@Preview(showBackground = false)
//@Composable
//fun MainPreview(){
//    MainView(rememberNavController())
//}