package com.wasterec.app.feature.importdataset.data

import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.wasterec.app.R

data class DatasetClass(
    var icon: Int,
    var className : String,
    var minimunCount : Int,
    var currentCount : Int,
    var maximumCount : Int
)

var datasetClassList : MutableList<DatasetClass> = mutableListOf(
    DatasetClass(R.drawable.plastic_waste, "Plastik", 12, currentCount = 0, maximumCount = 22 ),
    DatasetClass(R.drawable.paper_waste, "Kertas", 12, currentCount = 0, maximumCount = 22 ),
    DatasetClass(R.drawable.glass_waste, "Kaca", 12, currentCount = 0, maximumCount = 22 ),
    DatasetClass(R.drawable.metal_waste, "Logam", 12, currentCount = 0, maximumCount = 22 ),
    DatasetClass(R.drawable.cardboard_waste, "Kardus", 12, currentCount = 0, maximumCount = 22 ),
    DatasetClass(R.drawable.trash_waste, "Sampah", 12, currentCount = 0, maximumCount = 22 ),
)