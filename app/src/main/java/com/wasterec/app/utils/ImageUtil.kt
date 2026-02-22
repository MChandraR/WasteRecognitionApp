package com.wasterec.app.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import android.graphics.Matrix
import kotlin.math.min

fun resizeAndCropCenter(bitmap: Bitmap, targetSize: Int = 224): Bitmap {
    val width = bitmap.width
    val height = bitmap.height

    // 1. Hitung skala agar sisi terpendek menjadi 224 (atau targetSize)
    val scale = targetSize.toFloat() / min(width, height)

    val matrix = Matrix()
    matrix.postScale(scale, scale)

    // 2. Buat bitmap baru yang sudah di-skala
    val scaledBitmap = Bitmap.createBitmap(
        bitmap,
        0,
        0,
        width,
        height,
        matrix,
        true
    )

    // 3. Hitung koordinat crop (tengah)
    val cropX = (scaledBitmap.width - targetSize) / 2
    val cropY = (scaledBitmap.height - targetSize) / 2

    // 4. Potong bagian tengah
    return Bitmap.createBitmap(
        scaledBitmap,
        cropX,
        cropY,
        targetSize,
        targetSize
    )
}

fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
    return if (Build.VERSION.SDK_INT < 28) {
        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
    } else {
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        ImageDecoder.decodeBitmap(source)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun forceSoftwareBitmap(bitmap: Bitmap): Bitmap {
    if (bitmap.config != Bitmap.Config.HARDWARE) return bitmap

    val softwareBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false)
    bitmap.recycle()
    return softwareBitmap
}
