package com.wasterec.app.feature.importdataset.data

import com.wasterec.app.R

data class DatasetClass(
    var icon: Int,
    var className : String,
    var minimunCount : Int,
    var currentCount : Int,
    var maximumCount : Int
)

var datasetClassList : MutableList<DatasetClass> = mutableListOf(
    DatasetClass(R.drawable.plastic_waste, "Plastik", 0, currentCount = 0, maximumCount = 100 ),
    DatasetClass(R.drawable.paper_waste, "Kertas", 0, currentCount = 0, maximumCount = 100 ),
    DatasetClass(R.drawable.glass_waste, "Kaca", 0, currentCount = 0, maximumCount = 100 ),
    DatasetClass(R.drawable.metal_waste, "Logam", 0, currentCount = 0, maximumCount = 100 ),
    DatasetClass(R.drawable.cardboard_waste, "Kardus", 0, currentCount = 0, maximumCount = 100 ),
    DatasetClass(R.drawable.trash_waste, "Sampah", 0, currentCount = 0, maximumCount = 100 ),
)