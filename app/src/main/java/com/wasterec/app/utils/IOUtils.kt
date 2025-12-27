package com.wasterec.app.utils

import android.content.Context
import java.io.File

object IOUtils {
    public fun assetFilePath(context: Context, assetName: String): String {
        val file = File(context.filesDir, assetName)
        System.out.println("Ada file!")

        if (file.exists() && file.length() > 0) return file.absolutePath

        System.out.println("Tidak ada file!")
        context.assets.open(assetName).use { input ->
            file.outputStream().use { output -> input.copyTo(output) }
        }
        return file.absolutePath
    }
}