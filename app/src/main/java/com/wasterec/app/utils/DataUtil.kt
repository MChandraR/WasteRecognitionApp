package com.wasterec.app.utils

import android.util.Base64
import java.nio.ByteBuffer
import java.nio.ByteOrder

fun weightsToBase64(weights: Array<FloatArray>): List<String> {
    return weights.map { floatArrayToBase64(it) }
}

fun floatArrayToBase64(data: FloatArray): String {
    // 1 Float = 4 Byte
    val buffer = ByteBuffer.allocate(data.size * 4)
    buffer.order(ByteOrder.LITTLE_ENDIAN) // Wajib sama dengan Decoder

    // Masukkan semua data sekaligus (lebih cepat daripada loop manual)
    buffer.asFloatBuffer().put(data)

    return Base64.encodeToString(buffer.array(), Base64.NO_WRAP)
}

/**
 * Encode Array<FloatArray> (2 Dimensi) ke Base64 String.
 * Cocok untuk Weights.
 * * PERUBAHAN:
 * - Menghilangkan penyimpanan 'rows' dan 'cols' di dalam byte (Header 8 byte dihapus).
 * - Data disimpan flat (baris demi baris disambung).
 */
fun encodeWeightsToBase64(weights: Array<FloatArray>): String {
    if (weights.isEmpty()) return ""

    val rows = weights.size
    val cols = weights[0].size
    val totalFloats = rows * cols

    // HANYA alokasikan ruang untuk data float (tanpa tambahan 8 byte header)
    val buffer = ByteBuffer.allocate(totalFloats * 4)
    buffer.order(ByteOrder.LITTLE_ENDIAN)

    // Ratakan data (Flatten) langsung ke dalam buffer
    for (i in 0 until rows) {
        for (j in 0 until cols) {
            buffer.putFloat(weights[i][j])
        }
    }

    return Base64.encodeToString(buffer.array(), Base64.NO_WRAP)
}

fun decodeBase64ToFloatArray(base64String: String): FloatArray {
    // 1. Decode menggunakan android.util.Base64
    // Perbedaan: Kita harus memasukkan flag. DEFAULT aman untuk sebagian besar kasus.
    val bytes = Base64.decode(base64String, Base64.DEFAULT)

    // 2. Konversi ByteArray ke FloatBuffer (Sama seperti sebelumnya)
    // Gunakan Little Endian untuk kompatibilitas Android/TFLite
    val floatBuffer = ByteBuffer.wrap(bytes)
        .order(ByteOrder.LITTLE_ENDIAN)
        .asFloatBuffer()

    // 3. Salin ke FloatArray
    val floatArray = FloatArray(floatBuffer.remaining())
    floatBuffer.get(floatArray)

    return floatArray
}

/**
 * Fungsi ini logikanya sama persis, hanya memanggil fungsi decode di atas.
 * @param rows Baris (Output Channels)
 * @param cols Kolom (Input Channels)
 */
fun decodeBase64ToWeights(base64String: String, rows: Int, cols: Int): Array<FloatArray> {
    val flatArray = decodeBase64ToFloatArray(base64String)

    if (flatArray.size != rows * cols) {
        throw IllegalArgumentException("Size mismatch: Data ${flatArray.size} != Target ${rows * cols}")
    }

    return Array(rows) { rowIndex ->
        val start = rowIndex * cols
        val end = start + cols
        flatArray.sliceArray(start until end)
    }
}