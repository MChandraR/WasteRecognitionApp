package com.wasterec.app.feature.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wasterec.app.R
import com.wasterec.app.feature.home.view.HomeView
import com.wasterec.app.feature.home.viewmodel.HomeViewModel
import com.wasterec.app.feature.training.view.TrainingView
import com.wasterec.app.feature.training_history.view.TrainingHistoryView
import com.wasterec.app.feature.training_history.viewmodel.TrainingHistoryViewModel
import com.wasterec.app.model.Destination.Home
import com.wasterec.app.model.Destination.Info
import com.wasterec.app.model.Menu
import com.wasterec.app.ui.color.ColorAsset


@Composable
fun MainView(
    homeViewModel: HomeViewModel? = null,
    trainingHistoryViewModel: TrainingHistoryViewModel? = null
){
    val entries = arrayOf(
        Menu("Home", Home, R.drawable.outline_home_app_logo_24),
        Menu("Info", Info, R.drawable.outline_history_24)
    )

    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        
        bottomBar = {
            NavigationBar(windowInsets = NavigationBarDefaults.windowInsets, containerColor = ColorAsset.darkerWhite)  {
                entries.forEachIndexed { index, destination ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index
                        },
                        icon = {
                            Icon(
                                painterResource(destination.icon),
                                contentDescription = destination.label,
                                modifier = Modifier.size(40.dp)
                            )
                        },
                        label = {  },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent,
                            selectedIconColor = ColorAsset.primaryBlue,
                            unselectedIconColor = ColorAsset.secondaryBlue
                        )
                    )
                }
            }
        }
    ) { contentPadding ->
        Column(modifier = Modifier.background(Color.White)) {
            if (selectedTab == 0){
                HomeView(
                    homeViewModel,
                    modifier = Modifier.padding(contentPadding)
                )
            }else if (selectedTab == 1){
                TrainingHistoryView(
                    trainingHistoryViewModel
                )
            }
        }

    }


}

@Preview(showBackground = true)
@Composable
fun MainViewPreview(){
    MainView()
}