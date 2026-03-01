package com.wasterec.app.feature.importdataset.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.navigation.NavHostController
import com.wasterec.app.R
import com.wasterec.app.feature.importdataset.data.DatasetClass

class ImportDatasetViewModel( application : Application, val context: Context, val navHostController: NavHostController) : AndroidViewModel(application = application) {
    var datasetClassList : MutableList<DatasetClass> = mutableListOf(
        DatasetClass(R.drawable.plastic_waste, "Plastik", 12, currentCount = 0, maximumCount = 22 ),
        DatasetClass(R.drawable.paper_waste, "Kertas", 12, currentCount = 0, maximumCount = 22 ),
        DatasetClass(R.drawable.glass_waste, "Kaca", 12, currentCount = 0, maximumCount = 22 ),
        DatasetClass(R.drawable.metal_waste, "Logam", 12, currentCount = 0, maximumCount = 22 ),
        DatasetClass(R.drawable.cardboard_waste, "Kardus", 12, currentCount = 0, maximumCount = 22 ),
        DatasetClass(R.drawable.trash_waste, "Sampah", 12, currentCount = 0, maximumCount = 22 ),
    )

}