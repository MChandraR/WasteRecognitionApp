package com.wasterec.app.feature.classify.view

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wasterec.app.R
import com.wasterec.app.feature.classify.viewmodel.ClassifyViewModel
import com.wasterec.app.shared.components.MyButton
import com.wasterec.app.shared.components.SecondaryButton
import com.wasterec.app.ui.color.ColorAsset
import com.wasterec.app.ui.theme.Typography
import com.wasterec.app.utils.resizeWithEdgePadding
import com.wasterec.app.utils.uriToBitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale
import kotlin.coroutines.CoroutineContext

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ClassifyView(
    classifyViewModel: ClassifyViewModel? = null
){
    BackHandler{
        classifyViewModel?.backToHome()
    }

    LaunchedEffect(Dispatchers.IO){
        classifyViewModel?.init()
    }

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uris ->
        uris?.let{ uris ->
            uriToBitmap(context, uris)?.let{
                CoroutineScope(Dispatchers.Main).launch {
                    val bmp = resizeWithEdgePadding(it)
                    classifyViewModel?.currentBitMap?.value = bmp
                    classifyViewModel?.counter?.intValue += 1
                    classifyViewModel?.classifyImage(bmp)
                    println(classifyViewModel?.currentBitMap?.value)
                }

            }
        }
    }

    val file = remember {
        File(context.externalCacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
    }
    val uri =  FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )

    val cameraLauncher =  rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            val bitmap = resizeWithEdgePadding(BitmapFactory.decodeStream(
                context.contentResolver.openInputStream(uri)
            ))
            classifyViewModel?.currentBitMap?.value = bitmap
            classifyViewModel?.counter?.intValue++
            classifyViewModel?.classifyImage(bitmap)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            //cameraLauncher?.launch(uri)
        } else {
            Toast.makeText(context, "Izin kamera ditolak", Toast.LENGTH_SHORT).show()
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.weight(1f))

        Text(
            "Klasifikasi Sampah",
            fontSize = Typography.titleLarge.fontSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))


        if (classifyViewModel?.currentBitMap?.value != null &&  classifyViewModel.counter.intValue > 0 ) {
            classifyViewModel.currentBitMap?.value?.let { bmp ->
                // Jalankan inference di background saat URI berubah

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        bitmap = bmp.asImageBitmap(),
                        contentDescription = "Preview",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .padding(top = 16.dp)
                            .background(Color.Transparent)
                            .clip(RoundedCornerShape(10)),
                        contentScale = ContentScale.Crop
                    )

                }
            }
        } else {
            classifyViewModel?.predictedResult?.value = "Failed to convert bitmap"
            Box(
                modifier = Modifier.fillMaxWidth()
                    .background(ColorAsset.lightGray, shape = RoundedCornerShape(10))
                    .height(300.dp)
            ){

            }
        }

        Spacer(modifier = Modifier.weight(.5f))


        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                classifyViewModel?.predictedResult?.value ?: "Logam",
                textAlign = TextAlign.Center,
                fontSize = Typography.titleLarge.fontSize,
                fontWeight = FontWeight.Bold,
                color = ColorAsset.primaryBlue,
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 20.dp)
            )

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    "Confident Lv : ",
                    fontSize = Typography.titleLarge.fontSize,
                    textAlign = TextAlign.Center
                )
                Text(
                      "%.2f".format(Locale.ROOT, (classifyViewModel?.confidentLevel?.floatValue ?: 17.2937f ) * 100f)  + "%",
                    fontSize = Typography.titleLarge.fontSize,
                    color = ColorAsset.darkerBlue,
                    fontWeight = FontWeight.Bold
                )
            }
        }


        Spacer(Modifier.weight(1f))

        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
        ) {
            MyButton(
                onClick = {
                    classifyViewModel?.currentBitMap?.value?.let{ bmp->
                        classifyViewModel.classifyImage(bmp)
                    }
                },
                enabled = !(classifyViewModel?.isOnInference?.value ?: false) && classifyViewModel?.currentBitMap?.value != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.LightGray,
                        shape = RoundedCornerShape(10)
                    )
            ) {
                Text(
                    "Klasifikasi",
                    modifier = Modifier.padding(10.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                SecondaryButton(
                    onClick = {
                        launcher.launch("image/*")
                    },
                    enabled = !(classifyViewModel?.isOnInference?.value ?: false),
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            color = Color.Transparent,
                            shape = RoundedCornerShape(10)
                        ),
                    borderSize = 2,
                    borderColor = ColorAsset.primaryBlue
                ) {
                    Icon(painterResource(R.drawable.outline_download_24), "")
                    Text(
                        "Import",
                        modifier = Modifier.padding(10.dp)
                    )
                }

                Icon(painterResource(R.drawable.outline_photo_camera_24), "",
                    modifier = Modifier.size(55.dp).border(
                        2.dp,
                        color = ColorAsset.primaryBlue,
                        shape = RoundedCornerShape(10)
                    ).padding(10.dp)
                        .clickable(true){
                            val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)

                            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                                cameraLauncher.launch(uri)
                            } else {
                                // Minta izin
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }


                        }
                )

            }
        }

        Spacer(Modifier.weight(1f))

    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun ClassifyViewPreview(){
    ClassifyView()
}