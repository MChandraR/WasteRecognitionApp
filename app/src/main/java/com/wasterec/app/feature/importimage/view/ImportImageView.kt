package com.wasterec.app.feature.importimage.view

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.wasterec.app.R
import com.wasterec.app.feature.importimage.viewmodel.ImportImageViewModel
import com.wasterec.app.model.Destination
import com.wasterec.app.model.TrainingModel
import com.wasterec.app.ui.theme.Typography
import com.wasterec.app.utils.resizeAndCropCenter
import com.wasterec.app.utils.uriToBitmap

@Composable
fun ImportImageView(
    navHostControlelr : NavHostController? = null,
    importImageViewModel: ImportImageViewModel? = null
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        uris.forEach { uri ->
            val bitmap = uriToBitmap(context, uri)
            bitmap?.let {
                importImageViewModel?.imageDatasetList?.add(
                    TrainingModel(
                        resizeAndCropCenter(it),
                        (0..5).random()
                    )
                )
            }

        }
        println("Jumlah data dari picker : " + importImageViewModel?.imageDatasetList?.size.toString())
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {

        Spacer(modifier = Modifier.weight(.25f))

        Text(
            "Pilih 100 Gambar sebagai dataset pelatihan",
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            textAlign = TextAlign.Center,
            fontSize = Typography.titleLarge.fontSize,
            fontWeight = FontWeight.Medium
        )
        Text(
            "Jumlah gambar yang dipilih ${importImageViewModel?.imageDatasetList?.size}/100",
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            textAlign = TextAlign.Center,
            fontSize = Typography.bodyLarge.fontSize,
            fontWeight = FontWeight.Medium
        )


        if((importImageViewModel?.imageDatasetList?.size ?: 0) > 0){
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.weight(.5f)
            ) {
                items(importImageViewModel?.imageDatasetList?.size?:0) { index ->
                    importImageViewModel?.getImageDataBitmap(index)?.let{ bitmap ->
                        Box{
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .padding(4.dp)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(8.dp))
                            )

                            Button(
                                onClick = {

                                },
                                shape = CircleShape,
                                colors = ButtonColors(containerColor = Color.Transparent, contentColor = Color.Black, disabledContentColor = Color.Black, disabledContainerColor = Color.LightGray)
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.baseline_auto_delete_24),
                                    "Delete Icon"
                                )
                            }
                        }
                    }


                }
            }
        }else{
            Column (
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f).fillMaxWidth()
            ){
                Image(
                    painter = painterResource(R.drawable.add_photo_alternate_24),
                    "Image Empty",
                    modifier = Modifier.width(80.dp).height(80.dp),
                    alpha = 0.5f
                    )
                Text("Belum ada gambar",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

        }

        Spacer(modifier = Modifier.weight(.1f))

        Column{
            Button(
                onClick = {
                    launcher.launch("image/*")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, Color.Black),
                colors = ButtonColors(containerColor = Color.Transparent, contentColor = Color.Black, disabledContentColor = Color.LightGray, disabledContainerColor = Color.LightGray)
            ) {
                Text(
                    "Import gambar",
                    modifier = Modifier.padding(10.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    navHostControlelr?.navigate(Destination.Annotate)
                },
                enabled = (importImageViewModel?.imageDatasetList?.size?:0) >= 1,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonColors(containerColor = Color.LightGray, contentColor = Color.Black, disabledContentColor = Color.Black, disabledContainerColor = Color.LightGray)
            ) {
                Text(
                    "Selanjutnya",
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun ImportImageViewPreview(){
    Column(modifier = Modifier.background(Color.White)) {
        ImportImageView()
    }
}
