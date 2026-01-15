package com.wasterec.app.utils

import android.util.Base64
import java.nio.ByteBuffer
import java.nio.ByteOrder

class DataUtil {
}

fun weightsToBase64(weights: Array<FloatArray>): List<String> {
    return weights.map { floatArrayToBase64(it) }
}


fun floatArrayToBase64(data: FloatArray): String {
    val buffer = ByteBuffer.allocate(data.size * 4)
        .order(ByteOrder.LITTLE_ENDIAN)

    data.forEach { buffer.putFloat(it) }

    return Base64.encodeToString(buffer.array(), Base64.NO_WRAP)
}

fun encodeWeightsToBase64(weights: Array<FloatArray>): String {
    val rows = weights.size
    val cols = weights[0].size

    val buffer = ByteBuffer
        .allocate(8 + rows * cols * 4)
        .order(ByteOrder.LITTLE_ENDIAN)

    buffer.putInt(rows)
    buffer.putInt(cols)

    for (i in 0 until rows) {
        for (j in 0 until cols) {
            buffer.putFloat(weights[i][j])
        }
    }

    return Base64.encodeToString(buffer.array(), Base64.NO_WRAP)
}
