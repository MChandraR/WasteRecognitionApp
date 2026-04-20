package com.wasterec.app.feature.main

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wasterec.app.R
import com.wasterec.app.feature.home.view.HomeView
import com.wasterec.app.feature.home.viewmodel.HomeViewModel
import com.wasterec.app.feature.main.components.BottomNavBar
import com.wasterec.app.feature.training_history.view.TrainingHistoryView
import com.wasterec.app.feature.training_history.viewmodel.TrainingHistoryViewModel
import com.wasterec.app.ui.color.ColorAsset


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainView(
    homeViewModel: HomeViewModel? = null,
    trainingHistoryViewModel: TrainingHistoryViewModel? = null,
    mainViewModel: MainViewModel = MainViewModel()
){

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Aksi FAB */ },
                shape = CircleShape,
                containerColor = ColorAsset.darkerWhite, // Sesuaikan warna
                contentColor = Color.White,
                modifier = Modifier.offset(y = 60.dp).size(70.dp) // Mengatur FAB agar lebih "tenggelam"
            ) {
                Icon(painterResource(R.drawable.round_camera_alt_24), contentDescription = "Add",  modifier = Modifier.size(40.dp),  tint = ColorAsset.primaryBlue)
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = {
            BottomNavBar( mainViewModel.selectedTab)
        }
    ) { contentPadding ->
        Column(modifier = Modifier.background(Color.White)) {
            if (mainViewModel.selectedTab.intValue == 0){
                HomeView(
                    homeViewModel,
                    modifier = Modifier.padding(contentPadding)
                )
            }else if (mainViewModel.selectedTab.intValue == 1){
                TrainingHistoryView(
                    trainingHistoryViewModel
                )
            }
        }

    }


}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun MainViewPreview(){
    MainView()
}