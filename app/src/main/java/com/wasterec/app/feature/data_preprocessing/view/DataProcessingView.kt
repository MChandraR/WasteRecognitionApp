package com.wasterec.app.feature.data_preprocessing.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wasterec.app.feature.data_preprocessing.viewmodel.DataProcessingViewModel
import com.wasterec.app.shared.components.MyButton
import kotlinx.coroutines.Dispatchers

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DataProcessingView(
    dataProcessingViewModel: DataProcessingViewModel? = null
){
    LaunchedEffect(Dispatchers.IO) {
        dataProcessingViewModel?.proProcessData()
    }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Spacer(modifier = Modifier.weight(1f))

        MyButton(
            onClick = {
                dataProcessingViewModel?.navigateToAnnotatePage()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Selanjutnya",
                modifier = Modifier.padding(vertical = 10.dp)
                )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun DataProcessingViewPreview(){
    DataProcessingView()
}