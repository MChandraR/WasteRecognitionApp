package com.wasterec.app.feature.main.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateSet
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wasterec.app.R
import com.wasterec.app.feature.main.MainView
import com.wasterec.app.ui.color.ColorAsset

@Composable
fun BottomNavBar(selected: MutableIntState){
    val dynamicShape = getWideBottomBarShape(
        horizontalRadiusDp = 45.dp, // Cekungan lebar ke samping
        verticalDepthDp = 40.dp,    // Kedalaman yang pas untuk FAB melayang
        cornerRadiusDp = 18.dp
    )
    Surface(
        color = ColorAsset.darkerWhite,
        tonalElevation = 8.dp,
        shape = dynamicShape, // Menerapkan cekungan di sini
        modifier = Modifier.fillMaxWidth().height(90.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Item navigasi kiri
            IconButton(onClick = {
                selected.intValue = 0
            }) {
                Icon(painterResource(R.drawable.outline_home_app_logo_24),
                    "Home", modifier = Modifier.size(50.dp),
                    tint = if(selected.intValue == 0) ColorAsset.primaryBlue else ColorAsset.secondaryBlue
                )
            }

            Spacer(modifier = Modifier.width(60.dp)) // Beri ruang untuk FAB

            IconButton(onClick = {
                selected.intValue = 1
            }) {
                Icon(painterResource(R.drawable.outline_history_24),
                    "History", modifier = Modifier.size(50.dp),
                    tint = if(selected.intValue == 1) ColorAsset.primaryBlue else ColorAsset.secondaryBlue
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun previewMainView(){
    MainView()
}