package com.wasterec.app.presentation.views

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.wasterec.app.R
import com.wasterec.app.model.Destination.Home
import com.wasterec.app.model.Destination.Info
import com.wasterec.app.model.Menu


@Composable
fun MainView(
    navController : NavHostController
){
    val entries = arrayOf(
        Menu("Home", Home, R.drawable.baseline_home_24),
        Menu("Info", Info, R.drawable.baseline_add_location_24)
    )

    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                entries.forEachIndexed { index, destination ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index
                        },
                        icon = {
                            Icon(
                                painterResource(destination.icon),
                                contentDescription = destination.label
                            )
                        },
                        label = { Text(destination.label) }
                    )
                }
            }
        }
    ) { contentPadding ->
        if (selectedTab == 0){
            HomeView(
                navController = navController,
                modifier = Modifier.padding(contentPadding)
            )
        }
    }


}