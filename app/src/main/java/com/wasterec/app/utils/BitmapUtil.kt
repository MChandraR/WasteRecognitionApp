package com.wasterec.app.utils

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.scale
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * Mengonversi Bitmap ke FloatArray untuk input model ExecuTorch.
 * Menggunakan normalisasi ImageNet: mean=[0.485, 0.456, 0.406], std=[0.229, 0.224, 0.225]
 */
fun bitmapToFloatArray(bitmap: Bitmap, width: Int = 224, height: Int = 224): FloatArray {
    // 1. Resize bitmap ke ukuran input model (EfficientNet-B0 biasanya 224x224)
    val scaledBitmap = bitmap.scale(width, height)

    val imageSize = width * height
    val pixels = IntArray(imageSize)
    scaledBitmap.getPixels(pixels, 0, width, 0, 0, width, height)

    // 2. Siapkan array (Format: NCHW -> Channel First)
    // Index 0..imageSize-1: Red, imageSize..2*imageSize-1: Green, dst.
    val floatArray = FloatArray(3 * imageSize)

    for (i in 0 until imageSize) {
        val pix = pixels[i]

        // Extract RGB
        val r = (pix shr 16 and 0xFF) / 255.0f
        val g = (pix shr 8 and 0xFF) / 255.0f
        val b = (pix and 0xFF) / 255.0f

        // 3. Normalisasi (Standard ImageNet)
        floatArray[i] = (r - 0.485f) / 0.229f               // Red
        floatArray[i + imageSize] = (g - 0.456f) / 0.224f   // Green
        floatArray[i + 2 * imageSize] = (b - 0.406f) / 0.225f // Blue
    }

    return floatArray
}

fun bitmapToFloatBuffer(bitmap: Bitmap): FloatBuffer {
    val width = 224
    val height = 224
    val imageSize = width * height

    // Alokasikan Direct Buffer (4 byte per float * 3 channel * jumlah pixel)
    val outBuffer = ByteBuffer.allocateDirect(4 * 3 * imageSize)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()

    val pixels = IntArray(imageSize)
    bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

    // Normalisasi ImageNet
    val mean = floatArrayOf(0.485f, 0.456f, 0.406f)
    val std = floatArrayOf(0.229f, 0.224f, 0.225f)

    // Format NCHW (R dulu semua, lalu G, lalu B)
    for (i in 0 until imageSize) {
        val pix = pixels[i]
        outBuffer.put(i, ((pix shr 16 and 0xFF) / 255.0f - mean[0]) / std[0]) // R
        outBuffer.put(i + imageSize, ((pix shr 8 and 0xFF) / 255.0f - mean[1]) / std[1]) // G
        outBuffer.put(i + 2 * imageSize, ((pix and 0xFF) / 255.0f - mean[2]) / std[2]) // B
    }

    outBuffer.rewind()
    return outBuffer
}