package com.wasterec.app.presentation.views

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import android.os.Build
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.wasterec.app.manager.EfficientNetB0
import com.wasterec.app.model.Destination
import com.wasterec.app.model.TrainingModel
import com.wasterec.app.presentation.components.MyButton
import com.wasterec.app.ui.theme.Typography
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.graphics.ImageDecoder as AndroidImageDecoder

@Composable
fun AnnotateView(
    context: Context,
    navController : NavHostController,
    trainingData: SnapshotStateList<TrainingModel>
) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var predictResult by remember { mutableStateOf<String?>(null)}
    var confidentLevel by remember { mutableStateOf(0f)}
    var predictedLabel by remember { mutableStateOf(0)}

    val efficientNetB0 : EfficientNetB0 = EfficientNetB0(context, "model.ptl" )
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }
    var bitMap: Bitmap? = null
    val label = arrayOf("Glass", "Paper", "Cardboard",  "Metal","Plastic", "Trash")
    val context = LocalContext.current

    trainingData.clear()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.weight(1f))

        Text(
            "Training your model",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            "Need 20 Sample",
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 20.dp)
        )


        Spacer(modifier = Modifier.height(20.dp))

        val scope = rememberCoroutineScope()

        selectedImageUri?.let { uri ->
            val bitmap = remember(uri) {
                try {
                    if (Build.VERSION.SDK_INT < 28) {
                        BitmapFactory.decodeStream(
                            context.contentResolver.openInputStream(uri)
                        )
                    } else {
                        val source = AndroidImageDecoder.createSource(context.contentResolver, uri)
                        AndroidImageDecoder.decodeBitmap(source)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }

            bitmap?.let { bmp ->
                // Jalankan inference di background saat URI berubah
                bitMap = bmp
                LaunchedEffect(uri) {
                    predictResult = "Loading..."
                    withContext(Dispatchers.IO) {
                        val output = efficientNetB0.predict(
                            bmp.copy(Bitmap.Config.ARGB_8888, false)
                        )
                        val outputIdx = output.first
                        val labelResult = label.getOrNull(outputIdx)
                        withContext(Dispatchers.Main) {
                            predictResult = labelResult ?: "Unknown"
                            confidentLevel = output.second
                            if(confidentLevel == null || confidentLevel == 0.0f){
                                predictResult = "Unknown"

                            }
                            predictedLabel = outputIdx
                            Toast.makeText(context, "Berhasil " + confidentLevel.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }

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
        }

        Text(
            predictResult?: "Select Image to Classify",
            fontSize = Typography.displayLarge.fontSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        if ( predictResult != null) {
            Row(
                modifier = Modifier
                    .padding(vertical = 20.dp)
            ) {
                Text(
                    "Confident Lv.",
                    fontSize = Typography.titleLarge.fontSize
                )
                Text(
                    (confidentLevel * 100f).toString()+"%",
                    fontSize = Typography.titleLarge.fontSize,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.weight(1f))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ){
                MyButton(
                    onClick = {
                        imagePickerLauncher.launch("image/*")
                    },
                    modifier = Modifier
                        .background(
                            color = Color.LightGray,
                            shape = RoundedCornerShape(10)
                        )
                        .fillMaxWidth()
                ) {
                    Text("Input")
                }

                Spacer(Modifier.height(10.dp))

                MyButton(
                    onClick = {
                        if (bitMap != null) {
                            trainingData.add(
                                TrainingModel(bitMap!!.copy(Bitmap.Config.ARGB_8888,  false), predictedLabel)
                            )
                        }

                    },
                    modifier = Modifier
                        .background(
                            color = Color.LightGray,
                            shape = RoundedCornerShape(10)
                        )
                        .fillMaxWidth()
                ) {
                    Text("Correct")
                }
            }
            
            Spacer(Modifier.weight(.2f))

            Column(
                modifier = Modifier
                    .weight(1f),
                horizontalAlignment = Alignment.End
            ){
                MyButton(
                    onClick = {
                        imagePickerLauncher.launch("image/*")
                    },
                    modifier = Modifier
                        .background(
                            color = Color.LightGray,
                            shape = RoundedCornerShape(10)
                        )
                        .fillMaxWidth()
                ) {
                    Text("Predict")
                }

                Spacer(Modifier.height(10.dp))

                MyButton(
                    onClick = {
                        imagePickerLauncher.launch("image/*")
                    },
                    modifier = Modifier
                        .background(
                            color = Color.LightGray,
                            shape = RoundedCornerShape(10)
                        )
                        .fillMaxWidth()
                ) {
                    Text("Wrong")
                }
            }
        }

        Spacer(Modifier.weight(1f))

        MyButton(
            onClick = {
                navController.navigate(route = Destination.Training)
            },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                "Next"
            )
        }

    }
}


@Preview(showBackground = true)
@Composable
fun Prev(){
    //AnnotateView( rememberNavController())
}