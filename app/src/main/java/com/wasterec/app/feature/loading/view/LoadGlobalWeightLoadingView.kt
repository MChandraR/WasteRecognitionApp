package com.wasterec.app.feature.loading.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.wasterec.app.R
import com.wasterec.app.feature.loading.factory.LoadGlobalWeightLoadingViewModelFactory
import com.wasterec.app.feature.loading.viewmodel.LoadGlobalWeightLoadingViewModel
import com.wasterec.app.shared.components.GifLoader
import com.wasterec.app.ui.theme.Typography

@Composable
fun LoadGlobalWeightLoadingView(
    navHostController: NavHostController
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ){
        val loadGlobalWeightLoadingViewModel: LoadGlobalWeightLoadingViewModel = viewModel(
            factory = LoadGlobalWeightLoadingViewModelFactory(navHostController = navHostController)
        )

        LaunchedEffect(Unit) {
            loadGlobalWeightLoadingViewModel.fetchRecentGlobalModel()
        }

        Text("Loading Recent Global Weight", fontSize = Typography.titleMedium.fontSize)
        GifLoader(R.drawable.network)
    }
}

