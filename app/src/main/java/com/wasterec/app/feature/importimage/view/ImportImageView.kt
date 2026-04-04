package com.wasterec.app.feature.importimage.view

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.navigation.NavHostController
import com.wasterec.app.R
import com.wasterec.app.feature.importimage.components.ConfirmationDialog
import com.wasterec.app.feature.importimage.viewmodel.ImportImageViewModel
import com.wasterec.app.model.TrainingModel
import com.wasterec.app.shared.components.RoundedColoredBox
import com.wasterec.app.ui.color.ColorAsset
import com.wasterec.app.ui.theme.Typography
import com.wasterec.app.utils.resizeAndCropCenter
import com.wasterec.app.utils.uriToBitmap

@Composable
fun ImportImageView(
    navHostControlelr : NavHostController? = null,
    importImageViewModel: ImportImageViewModel? = null,
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        val datasetCount = (importImageViewModel?.imageDatasetList?.size)?:0
        uris.forEachIndexed { idx,uri ->

            if( ((importImageViewModel?.getTotalOfDatasetForSelectedLabel()) ?: 0).plus(idx) < (importImageViewModel?.selectedLabel?.value?.maximumCount
                    ?: 0)
            ){
                val bitmap = uriToBitmap(context, uri)
                bitmap?.let {
                    importImageViewModel?.increaseItemCountForSelectedLabelinDataset()
                    importImageViewModel?.imageDatasetList?.add(
                        TrainingModel(
                            resizeAndCropCenter(it),
                            importImageViewModel.selectedLabelIndex.value
                        )
                    )
                }
            }


        }
        println("Jumlah data dari picker : " + importImageViewModel?.imageDatasetList?.size.toString())
    }

    Box(Modifier.fillMaxSize().padding(16.dp)) {
        Column(Modifier.fillMaxSize()) {

            Spacer(modifier = Modifier.weight(.25f))

            Text(
                "Import Dataset untuk label ${importImageViewModel?.getSelectedLabel()}",
                modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp),
                textAlign = TextAlign.Center,
                fontSize = Typography.titleLarge.fontSize,
                fontWeight = FontWeight.Bold,
                color = ColorAsset.primaryBlue,
            )

            Spacer(modifier = Modifier.height(40.dp))

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Jumlah gambar :",
                        modifier = Modifier,
                        textAlign = TextAlign.Center,
                        fontSize = Typography.bodyLarge.fontSize,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        " ${(importImageViewModel?.getTotalOfDatasetForSelectedLabel()) ?: 0}",
                        modifier = Modifier,
                        textAlign = TextAlign.Center,
                        fontSize = Typography.titleLarge.fontSize,
                        fontWeight = FontWeight.Bold,
                        color = ColorAsset.primaryBlue
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    RoundedColoredBox(
                        "Min:", "${importImageViewModel?.selectedLabel?.value?.minimunCount}",
                        color = ColorAsset.primaryYellow, 5,
                        modifier = Modifier.height(40.dp),
                        leadingFontSize = Typography.titleSmall.fontSize,
                        trailingFontSize = Typography.titleSmall.fontSize,
                    )

                    RoundedColoredBox(
                        "Max:", "${importImageViewModel?.selectedLabel?.value?.maximumCount}",
                        color = ColorAsset.primaryRed, 5,
                        modifier = Modifier.height(40.dp),
                        leadingFontSize = Typography.titleSmall.fontSize,
                        trailingFontSize = Typography.titleSmall.fontSize,
                    )
                }
            }



            if ((importImageViewModel?.getDatasetForSelectedLabel()?.size ?: 0) > 0) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.weight(.5f)
                ) {
                    items(importImageViewModel?.getDatasetForSelectedLabel()?.size ?: 0) { index ->
                        importImageViewModel?.getDatasetForSelectedLabel()[index]?.Input?.let { bitmap ->
                            Box {
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
                                        importImageViewModel.selectedImageIndex.value = index
                                        importImageViewModel.showConfirmImageDeletionDialog.value = true
                                    },
                                    shape = CircleShape,
                                    colors = ButtonColors(
                                        containerColor = Color.Transparent,
                                        contentColor = Color.Black,
                                        disabledContentColor = Color.Black,
                                        disabledContainerColor = Color.LightGray
                                    )
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
            } else {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f).fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(R.drawable.add_photo_alternate_24),
                        "Image Empty",
                        modifier = Modifier.width(80.dp).height(80.dp),
                        alpha = 0.5f
                    )
                    Text(
                        "Belum ada gambar",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

            }

            Spacer(modifier = Modifier.weight(.1f))

            Column {
                Button(
                    onClick = {
                        importImageViewModel?.validateDataForCurrentLabelBeforeInput {
                            launcher.launch("image/*")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(3.dp, ColorAsset.primaryBlue),
                    colors = ButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = ColorAsset.primaryBlue,
                        disabledContentColor = Color.LightGray,
                        disabledContainerColor = Color.LightGray
                    )
                ) {
                    Text(
                        "Import gambar",
                        fontSize = Typography.bodyLarge.fontSize,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        navHostControlelr?.popBackStack()
                    },
                    enabled = (importImageViewModel?.imageDatasetList?.size ?: 0) >= 1,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonColors(
                        containerColor = ColorAsset.primaryBlue,
                        contentColor = Color.White,
                        disabledContentColor = Color.Black,
                        disabledContainerColor = Color.LightGray
                    )
                ) {
                    Text(
                        "Simpan",
                        fontSize = Typography.bodyLarge.fontSize,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }

        //Menampilkan konfirmasi dialog ketika menghapus data
        if(importImageViewModel?.showConfirmImageDeletionDialog?.value == true) {
            ConfirmationDialog(
                onConfirm = {
                    importImageViewModel.deleteDataFromDataset(importImageViewModel.selectedImageIndex.value)
                    importImageViewModel.showConfirmImageDeletionDialog.value = false
                    importImageViewModel.increaseItemCountForSelectedLabelinDataset()

                },
                onDismiss = {
                    importImageViewModel.showConfirmImageDeletionDialog.value = false
                }
            )
        }
    }
}

@Preview
@Composable
fun ImportImageViewPreview(){
    Column(modifier = Modifier.background(Color.White)) {
        ImportImageView(null, null)
    }
}
