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
import kotlin.random.Random

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

/**
 * Memutar bitmap berdasarkan derajat tertentu (misal: 90f, 180f, 270f).
 */
fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(degrees)
    return Bitmap.createBitmap(
        bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
    )
}

fun flipHorizontal(bitmap: Bitmap): Bitmap {
    val matrix = Matrix()
    // Scale -1 pada sumbu X akan membalikkan gambar secara horizontal
    matrix.postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
    return Bitmap.createBitmap(
        bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
    )
}

fun flipVertical(bitmap: Bitmap): Bitmap {
    val matrix = Matrix()
    // Scale 1f pada X (tetap), -1f pada Y (terbalik secara vertikal)
    matrix.postScale(1f, -1f, bitmap.width / 2f, bitmap.height / 2f)

    return Bitmap.createBitmap(
        bitmap,
        0,
        0,
        bitmap.width,
        bitmap.height,
        matrix,
        true
    )
}

fun applyRandomAugmentation(bitmap: Bitmap): Bitmap {
    var result = bitmap

    // Acak Flip (50% peluang)
    if ((0..1).random() == 1) {
        result = flipHorizontal(result)
    }

    // Acak Rotasi (Pilih salah satu sudut)
    val angles = listOf(0f, 90f, 180f, 270f)
    val randomAngle = angles.random()
    if (randomAngle != 0f) {
        result = rotateBitmap(result, randomAngle)
    }

    return result
}

fun generateBooleanWithChance(chanceOfTrue: Double): Boolean {
    require(chanceOfTrue in 0.0..1.0) { "Chance must be between 0.0 and 1.0" }
    return Random.nextDouble() < chanceOfTrue
}
