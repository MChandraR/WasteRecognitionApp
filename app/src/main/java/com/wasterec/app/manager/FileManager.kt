package com.wasterec.app.manager

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream

class FileManager(val context : Context) {
    suspend fun saveDownloadFileToDisk(fileName : String, responseBody: ResponseBody, onProgress : (progress : Float)->Unit){
        return withContext(Dispatchers.IO){
            val destinationFile : File = File(context.filesDir, fileName)

            var inputStream : InputStream? = null
            var fileOutputStream : FileOutputStream? = null

            try {
                inputStream = responseBody.byteStream()
                fileOutputStream = FileOutputStream(destinationFile)

                val buffer = ByteArray(4096)
                var byteRead : Int
                var totalByteRead : Long = 0
                var fileSize = responseBody.contentLength()


                Log.d("Download", "Mulai download ke: ${destinationFile.absolutePath}")

                while(inputStream.read(buffer).also { byteRead = it } != -1){
                    fileOutputStream.write(buffer, 0, byteRead)
                    totalByteRead += byteRead
                    onProgress(totalByteRead*1f /fileSize)
                }

                Log.d("Download", "Download Selesai! Ukuran: $totalByteRead bytes")

            }catch (e: Exception) {
                Log.e("Download", "Gagal menyimpan file: ${e.message}")
                destinationFile.delete() // Hapus file corrupt jika gagal
                throw e
            } finally {
                inputStream?.close()
                fileOutputStream?.close()
            }
        }
    }
}