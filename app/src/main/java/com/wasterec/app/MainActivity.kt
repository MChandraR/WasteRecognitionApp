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
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.wasterec.app.feature.anotate.factory.AnotateViewModelFactory
import com.wasterec.app.feature.anotate.viewmodel.AnotateViewModel
import com.wasterec.app.feature.home.view_model_factory.HomeViewModelFactory
import com.wasterec.app.feature.home.viewmodel.HomeViewModel
import com.wasterec.app.feature.importdataset.viewmodel.ImportDatasetViewModel
import com.wasterec.app.feature.importdataset.viewmodel_factory.ImportDatasetViewModelFactory
import com.wasterec.app.feature.importimage.viewmodel.ImportImageViewModel
import com.wasterec.app.feature.login.factory.LoginViewModelFactory
import com.wasterec.app.feature.login.viewmodel.LoginViewModel
import com.wasterec.app.feature.modelload.factory.ModelLoadViewModelFactory
import com.wasterec.app.feature.modelload.viewmodel.ModelLoadViewModel
import com.wasterec.app.feature.navigation.view.NavigationView
import com.wasterec.app.feature.training.viewmodel.TrainingViewModel
import com.wasterec.app.feature.training.viewmodelfactory.TrainingViewModelFactory
import com.wasterec.app.model.Destination

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val handler = Handler(Looper.getMainLooper())

        setContent {
            val navController = rememberNavController()
            val application: Application = this.applicationContext as Application

            val importImageViewModel : ImportImageViewModel = viewModel()
            val anotateViewModel : AnotateViewModel = viewModel(
                factory = AnotateViewModelFactory(
                    application = application,
                    context = this,
                    navHostController = navController
                )
            )

            val trainingViewModel : TrainingViewModel = viewModel(
                factory = TrainingViewModelFactory(
                    application = application,
                    context = this,
                    navHostController = navController,
                    importImageViewModel = importImageViewModel
                )
            )

            val modelLoadViewModel: ModelLoadViewModel = viewModel(
                factory = ModelLoadViewModelFactory(
                    application = application,
                    context = this,
                    navHostController = navController,
                    trainingViewModel = trainingViewModel
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
                    navHostController = navController
                )
            )

            //DebugView(this)
            NavigationView(
                this,
                navController,
                loginViewModel,
                homeViewModel,
                importDatasetViewModel,
                anotateViewModel,
                importImageViewModel,
                trainingViewModel,
                modelLoadViewModel,
            )

            handler.postDelayed({
                navController.navigate(Destination.Login) {
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