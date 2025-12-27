package com.wasterec.app.presentation.components
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import android.os.Build
import androidx.compose.ui.platform.LocalContext


@Composable
fun GifLoader(data : Int) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(data = data) // nama file gif
            .decoderFactory(
                if (Build.VERSION.SDK_INT >= 28) {
                    ImageDecoderDecoder.Factory()
                } else {
                    GifDecoder.Factory()
                }
            )
            .build(),
        contentDescription = "Animasi GIF",
        modifier = Modifier.size(200.dp)
    )
}