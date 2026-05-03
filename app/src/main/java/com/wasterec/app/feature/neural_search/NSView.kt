package com.wasterec.app.feature.neural_search

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.Dispatchers

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NSView(nsViewModel: NSViewModel){

    LaunchedEffect(Dispatchers.IO) {
        //nsViewModel.loadDataset()
    }


    Column(modifier = Modifier.fillMaxSize()) {


//        TextField(nsViewModel.stringText.value, onValueChange = {
//            nsViewModel.stringText.value = it
//        }, modifier = Modifier.fillMaxSize())

        Button(
            {
                nsViewModel.loadDataset()
            }
        ) {
            Text("Aha")
        }

        Text(nsViewModel.minVal.value.toString())

    }
}

@Preview(showBackground = true)
@Composable
fun previewNS(){
    Column(modifier = Modifier.fillMaxSize()) {


        TextField("", onValueChange = {

        }, modifier = Modifier.fillMaxSize())


    }
}