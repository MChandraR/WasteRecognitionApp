package com.wasterec.app.feature.importdataset.view

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wasterec.app.feature.importdataset.components.LabelListCard
import com.wasterec.app.feature.importdataset.data.datasetClassList
import com.wasterec.app.feature.importdataset.viewmodel.ImportDatasetViewModel
import com.wasterec.app.feature.training.ui.trainingColorList
import com.wasterec.app.model.Destination
import com.wasterec.app.shared.components.MyButton
import com.wasterec.app.ui.color.ColorAsset
import com.wasterec.app.ui.theme.Typography
import kotlinx.coroutines.Dispatchers

@Composable
fun ImportDatasetView(
    importDatasetViewModel: ImportDatasetViewModel? = null,
){
    BackHandler() {
        
    }

    LaunchedEffect(Dispatchers.IO) {
        importDatasetViewModel?.getClassCount()
        importDatasetViewModel?.validateClassCount()
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Text(buildAnnotatedString {
            append("Pilih")
            withStyle(style = SpanStyle(fontSize = Typography.titleMedium.fontSize,color = ColorAsset.primaryBlue, fontWeight = FontWeight.Bold)) {
                append(" 100 ")
            }
            append("gambar sebagai dataset pelatihan")
        },
            fontSize = Typography.titleLarge.fontSize,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp)
        )
        Text(buildAnnotatedString {
            append("Jumlah gambar dipilih ")
            withStyle(style = SpanStyle(fontSize = Typography.titleLarge.fontSize, fontWeight = FontWeight.Bold, color = ColorAsset.primaryYellow)){
                append("${importDatasetViewModel?.getTrainingDatasetCount() ?: 0}")
            }
            withStyle(style = SpanStyle(fontSize = Typography.titleMedium.fontSize,fontWeight = FontWeight.Bold, color = ColorAsset.primaryBlue)){
                append("/100")
            }
        },
            fontSize = Typography.titleMedium.fontSize,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(.5f))

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(10.dp)
        ) {
            (importDatasetViewModel?.datasetClassList?: datasetClassList).forEachIndexed { idx,value ->
                LabelListCard(
                    value.className,
                    "(${ (importDatasetViewModel?.classCount?.get(idx))?:0 } gambar)",
                    leadingIcon = value.icon,
                    modifier = Modifier.height(75.dp).clickable{
                        importDatasetViewModel?.resetState()
                        importDatasetViewModel?.selectedIndex?.value = importDatasetViewModel.datasetClassList.indexOf(value)
                        importDatasetViewModel?.navHostController?.navigate(Destination.Import)
                    }
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))

        MyButton(
            modifier = Modifier.padding(10.dp).fillMaxWidth(),
            enabled = ((importDatasetViewModel?.trainignDataset?.size?:0) >= 100 && (importDatasetViewModel?.isClassCountMeetRequirement?.value?: true) )  || true,
            onClick = {
                    importDatasetViewModel?.navHostController?.navigate(Destination.Preprocess)
            }
        ) {
            Text("Selanjutnya",
                fontSize = Typography.titleMedium.fontSize,
                modifier = Modifier.padding(10.dp))
        }

    }
}

@Preview(showBackground = true)
@Composable
fun ImportDatasetViewPreview(){
    ImportDatasetView(null)
}