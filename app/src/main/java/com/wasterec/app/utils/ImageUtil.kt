package com.wasterec.app.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import kotlin.math.max
import kotlin.random.Random
import androidx.core.graphics.get
import androidx.core.graphics.scale

@RequiresApi(Build.VERSION_CODES.O)
fun resizeWithEdgePadding(bitmap: Bitmap, targetSize: Int = 224): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    val bmp = forceSoftwareBitmap(bitmap)

    // 1. Hitung rasio skala agar sisi terpanjang pas dengan targetSize (224)
    val scale = targetSize.toFloat() / max(width, height)
    val scaledWidth = (width * scale).toInt()
    val scaledHeight = (height * scale).toInt()

    // 2. Resize gambar asli
    val resizedBitmap = Bitmap.createScaledBitmap(bmp, scaledWidth, scaledHeight, true)

    // 3. Buat output bitmap (224x224)
    val outputBitmap = Bitmap.createBitmap(targetSize, targetSize, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(outputBitmap)

    // 4. Setup Paint dengan BitmapShader mode CLAMP
    val paint = Paint(Paint.FILTER_BITMAP_FLAG)
    val shader = BitmapShader(resizedBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

    // 5. Atur posisi shader agar gambar resized ada di tengah kanvas
    val left = (targetSize - scaledWidth) / 2f
    val top = (targetSize - scaledHeight) / 2f
    val matrix = Matrix()
    matrix.postTranslate(left, top)
    shader.setLocalMatrix(matrix)

    paint.shader = shader

    // 6. Gambar persegi penuh 224x224. 
    // Area di luar resizedBitmap akan diisi oleh piksel pinggir yang ditarik.
    canvas.drawRect(0f, 0f, targetSize.toFloat(), targetSize.toFloat(), paint)

    return outputBitmap
}

fun getAverageColor(bitmap: Bitmap): Int {
    // Kita perkecil gambar dulu ke 1x1 pixel agar Android menghitung rata-ratanya secara otomatis
    // Ini metode tercepat daripada melakukan looping pixel secara manual
    val tinyBitmap = bitmap.scale(10, 10)
    val color = tinyBitmap[0, 0]
    tinyBitmap.recycle()
    return color
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
    // 1. If it's already software-backed or already recycled, just return it
    if (bitmap.isRecycled || bitmap.config != Bitmap.Config.HARDWARE) {
        return bitmap
    }

    // 2. Attempt to copy. Note: copy() can return null if out of memory
    val softwareBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false)

    // 3. Only recycle the old one if the copy was successful
    // AND you are sure no other part of your app needs the hardware version.
    if (softwareBitmap != null) {
        // bitmap.recycle() // Optional: Only use if you're sure you're done with 'bitmap'
        return softwareBitmap
    }

    return bitmap
}

/**
 * Memutar bitmap berdasarkan derajat tertentu (misal: 90f, 180f, 270f).
 */

fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(degrees)

    // 1. Hitung ukuran bounding box baru agar gambar tidak terpotong
    val rect = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
    matrix.mapRect(rect)

    val newWidth = Math.round(rect.width())
    val newHeight = Math.round(rect.height())

    // 2. Buat output bitmap
    val outputBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(outputBitmap)

    // 3. Setup Paint dengan TileMode.CLAMP (ini yang menarik piksel ujung)
    val paint = Paint(Paint.FILTER_BITMAP_FLAG)
    val shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

    // Matrix untuk shader agar ikut berputar dan berpindah ke tengah
    val shaderMatrix = Matrix()
    shaderMatrix.postRotate(degrees, bitmap.width / 2f, bitmap.height / 2f)
    shaderMatrix.postTranslate((newWidth - bitmap.width) / 2f, (newHeight - bitmap.height) / 2f)
    shader.setLocalMatrix(shaderMatrix)

    paint.shader = shader

    // 4. Gambar persegi panjang sebesar ukuran baru, shader akan otomatis mengisi tepiannya
    canvas.drawRect(0f, 0f, newWidth.toFloat(), newHeight.toFloat(), paint)

    return outputBitmap
}

fun flipHorizontal(bitmap: Bitmap): Bitmap {
    val outputBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(outputBitmap)

    val paint = Paint(Paint.FILTER_BITMAP_FLAG)
    val shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

    val matrix = Matrix()
    matrix.postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
    shader.setLocalMatrix(matrix)

    paint.shader = shader
    canvas.drawRect(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat(), paint)

    return outputBitmap
}

@RequiresApi(Build.VERSION_CODES.O)
fun applyRandomCrop(bitmaps: Bitmap, minScale: Float = 0.8f): Bitmap {
    val bitmap = forceSoftwareBitmap(bitmaps)
    val width = bitmap.width
    val height = bitmap.height

    // 1. Tentukan ukuran area yang akan di-crop
    // minScale 0.8f berarti kita mengambil minimal 80% dari gambar asli (zoom in)
    val scale = Random.nextFloat() * (1f - minScale) + minScale
    val cropWidth = (width * scale).toInt()
    val cropHeight = (height * scale).toInt()

    // 2. Tentukan titik koordinat (x, y) awal secara acak
    val left = Random.nextInt(0, width - cropWidth + 1)
    val top = Random.nextInt(0, height - cropHeight + 1)

    val sourceRect = Rect(left, top, left + cropWidth, top + cropHeight)
    val destRect = Rect(0, 0, width, height)

    // 3. Buat bitmap baru dan gambar area crop ke ukuran penuh
    val outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(outputBitmap)

    // Paint dengan FILTER_BITMAP_FLAG agar hasil zoom tidak pecah/pixelated
    val paint = android.graphics.Paint(android.graphics.Paint.FILTER_BITMAP_FLAG)

    canvas.drawBitmap(bitmap, sourceRect, destRect, paint)

    return outputBitmap
}

fun flipVertical(bitmap: Bitmap): Bitmap {
    val outputBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(outputBitmap)

    val paint = Paint(Paint.FILTER_BITMAP_FLAG)
    val shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

    val matrix = Matrix()
    matrix.postScale(1f, -1f, bitmap.width / 2f, bitmap.height / 2f)
    shader.setLocalMatrix(matrix)

    paint.shader = shader
    canvas.drawRect(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat(), paint)

    return outputBitmap
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



//fun resizeAndCropCenter(bitmap: Bitmap, targetSize: Int = 224): Bitmap {
//    val width = bitmap.width
//    val height = bitmap.height
//
//    // 1. Hitung skala agar sisi terpendek menjadi 224 (atau targetSize)
//    val scale = targetSize.toFloat() / min(width, height)
//
//    val matrix = Matrix()
//    matrix.postScale(scale, scale)
//
//    // 2. Buat bitmap baru yang sudah di-skala
//    val scaledBitmap = Bitmap.createBitmap(
//        bitmap,
//        0,
//        0,
//        width,
//        height,
//        matrix,
//        true
//    )
//
//    // 3. Hitung koordinat crop (tengah)
//    val cropX = (scaledBitmap.width - targetSize) / 2
//    val cropY = (scaledBitmap.height - targetSize) / 2
//
//    // 4. Potong bagian tengah
//    return Bitmap.createBitmap(
//        scaledBitmap,
//        cropX,
//        cropY,
//        targetSize,
//        targetSize
//    )
//}

//
//@RequiresApi(Build.VERSION_CODES.O)
//fun resizeWithMeanPadding(bitmap: Bitmap, targetSize: Int = 224): Bitmap {
//    val width = bitmap.width
//    val height = bitmap.height
//    var bmp = forceSoftwareBitmap(bitmap)
//
//    // 1. Hitung warna rata-rata dari gambar asli
//    val meanColor = getAverageColor(bmp)
//
//    // 2. Hitung rasio skala agar gambar muat di dalam targetSize tanpa distorsi
//    val scale = targetSize.toFloat() / max(width, height)
//    val scaledWidth = (width * scale).toInt()
//    val scaledHeight = (height * scale).toInt()
//
//    // 3. Resize gambar asli
//    val resizedBitmap = Bitmap.createScaledBitmap(bmp, scaledWidth, scaledHeight, true)
//
//    // 4. Buat kanvas ukuran targetSize x targetSize
//    val outputBitmap = Bitmap.createBitmap(targetSize, targetSize, Bitmap.Config.ARGB_8888)
//    val canvas = Canvas(outputBitmap)
//
//    // 5. Isi background dengan warna rata-rata yang sudah dihitung
//    canvas.drawColor(meanColor)
//
//    // 6. Letakkan gambar di tengah
//    val left = (targetSize - scaledWidth) / 2f
//    val top = (targetSize - scaledHeight) / 2f
//    canvas.drawBitmap(resizedBitmap, left, top, null)
//
//    return outputBitmap
//}