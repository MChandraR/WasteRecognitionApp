package com.wasterec.app.feature.main

import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {
    var selectedTab  = mutableIntStateOf(0)
}