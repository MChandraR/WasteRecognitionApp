package com.wasterec.app

import android.app.Application
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.wasterec.app.feature.anotate.factory.AnotateViewModelFactory
import com.wasterec.app.feature.anotate.viewmodel.AnnotateViewModel
import com.wasterec.app.feature.data_preprocessing.viewmodel.DataProcessingViewModel
import com.wasterec.app.feature.data_preprocessing.viewmodel_factory.DataProcessingViewModelFactory
import com.wasterec.app.feature.home.view_model_factory.HomeViewModelFactory
import com.wasterec.app.feature.home.viewmodel.HomeViewModel
import com.wasterec.app.feature.importdataset.data.DatasetClass
import com.wasterec.app.feature.importdataset.data.datasetClassList
import com.wasterec.app.feature.importdataset.viewmodel.ImportDatasetViewModel
import com.wasterec.app.feature.importdataset.viewmodel_factory.ImportDatasetViewModelFactory
import com.wasterec.app.feature.importimage.viewmodel.ImportImageViewModel
import com.wasterec.app.feature.importimage.viewmodel_factory.ImportImageViewModelFactory
import com.wasterec.app.feature.login.factory.LoginViewModelFactory
import com.wasterec.app.feature.login.viewmodel.LoginViewModel
import com.wasterec.app.feature.modelload.factory.ModelLoadViewModelFactory
import com.wasterec.app.feature.modelload.viewmodel.ModelLoadViewModel
import com.wasterec.app.feature.navigation.view.NavigationView
import com.wasterec.app.feature.training.viewmodel.FinishTrainingViewModel
import com.wasterec.app.feature.training.viewmodel.TrainingViewModel
import com.wasterec.app.feature.training.viewmodelfactory.FinishTrainingViewModelFactory
import com.wasterec.app.feature.training.viewmodelfactory.TrainingViewModelFactory
import com.wasterec.app.manager.DatasetManager
import com.wasterec.app.model.Destination
import com.wasterec.app.model.TrainingModel

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val handler = Handler(Looper.getMainLooper())

        setContent {
            val navController = rememberNavController()
            val application: Application = this.applicationContext as Application
            val trainingData : SnapshotStateList<TrainingModel> = remember {mutableStateListOf()}
            val selectedLabelIndex : MutableState<Int> = remember {mutableStateOf(0)}
            val selectedLabel : MutableState<DatasetClass> = remember {mutableStateOf(
                datasetClassList[0]
            )}
            val datasetManager: MutableState<DatasetManager> = remember { mutableStateOf(DatasetManager(listOf()))}

            var datasetClassList : SnapshotStateList<DatasetClass> = remember {
                mutableStateListOf(
                    DatasetClass(
                        R.drawable.plastic_waste,
                        "Plastik",
                        12,
                        currentCount = 0,
                        maximumCount = 22
                    ),
                    DatasetClass(
                        R.drawable.paper_waste,
                        "Kertas",
                        12,
                        currentCount = 0,
                        maximumCount = 22
                    ),
                    DatasetClass(
                        R.drawable.glass_waste,
                        "Kaca",
                        12,
                        currentCount = 0,
                        maximumCount = 22
                    ),
                    DatasetClass(
                        R.drawable.metal_waste,
                        "Logam",
                        12,
                        currentCount = 0,
                        maximumCount = 22
                    ),
                    DatasetClass(
                        R.drawable.cardboard_waste,
                        "Kardus",
                        12,
                        currentCount = 0,
                        maximumCount = 22
                    ),
                    DatasetClass(
                        R.drawable.trash_waste,
                        "Sampah",
                        12,
                        currentCount = 0,
                        maximumCount = 22
                    ),
                )
            }

            val importImageViewModel : ImportImageViewModel = viewModel(
                factory = ImportImageViewModelFactory(
                    application = application,
                    trainingData,
                    selectedLabelIndex,
                    selectedLabel
                )
            )
            val annotateViewModel : AnnotateViewModel = viewModel(
                factory = AnotateViewModelFactory(
                    application = application,
                    context = this,
                    navHostController = navController,
                    trainingData
                )
            )

            val trainingViewModel : TrainingViewModel = viewModel(
                factory = TrainingViewModelFactory(
                    application = application,
                    context = this,
                    navHostController = navController,
                    annotateViewModel = annotateViewModel,
                    datasetManager = datasetManager
                )
            )

            val modelLoadViewModel: ModelLoadViewModel = viewModel(
                factory = ModelLoadViewModelFactory(
                    application = application,
                    context = this,
                    navHostController = navController
                )
            )

            val homeViewModel : HomeViewModel = viewModel(
                factory = HomeViewModelFactory(
                    application = application,
                    context = this,
                    navHostController = navController
                )
            )

            val loginViewModel : LoginViewModel = viewModel(
                factory = LoginViewModelFactory(
                    app = application,
                    context = this,
                    navHostController = navController
                )
            )

            val importDatasetViewModel: ImportDatasetViewModel = viewModel(
                factory = ImportDatasetViewModelFactory(
                    application = application,
                    context = this,
                    navHostController = navController,
                    trainingData,
                    selectedLabelIndex,
                    selectedLabel,
                    datasetClassList
                )
            )

            val datasetProcessingViewModel : DataProcessingViewModel = viewModel(
                factory = DataProcessingViewModelFactory(
                    application,
                    navController,
                    trainingData,
                    datasetManager
                )
            )

            val finishTrainingViewModel : FinishTrainingViewModel = viewModel(
                factory = FinishTrainingViewModelFactory(
                    application,
                    navController,
                    trainingData,
                    datasetManager
                )
            )

            //DebugView(this)
            NavigationView(
                this,
                navController,
                loginViewModel,
                homeViewModel,
                importDatasetViewModel,
                datasetProcessingViewModel,
                annotateViewModel,
                importImageViewModel,
                trainingViewModel,
                modelLoadViewModel,
                finishTrainingViewModel
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