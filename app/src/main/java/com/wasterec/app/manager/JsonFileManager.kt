package com.wasterec.app.manager

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wasterec.app.model.ClassifierWeightModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.lang.Exception

class JsonFileManager<T>(val context : Context, val fileName : String ) {
    val filename = fileName
    val gson : Gson = Gson()

    suspend fun saveJsonFiles(data : T){
        withContext(Dispatchers.IO){

            try{
                val file = File(context.filesDir, filename)

                val writer = FileWriter(file)
                gson.toJson(data, writer)
                writer.flush()
                writer.close()
                println("Berhasil menyimpan file json dari param classifier")
            }catch(e : Exception){
                println(e.message)
                println("Gagal menyimpan classifier param ke json")
            }
        }
    }

    suspend inline fun <reified T> loadJsonFile(): T? {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(context.filesDir, filename)

                if (!file.exists()) {
                    println("⚠️ File $filename tidak ditemukan")
                    return@withContext null
                }

                // Gunakan .use { ... } untuk otomatis menutup FileReader (AutoCloseable)
                FileReader(file).use { fileReader ->
                    val type = object : TypeToken<T>(){}.type
                    val classifierParam : T = gson.fromJson(fileReader,type)

                    println("✅ Sukses load model dari JSON. $classifierParam")
                    classifierParam
                }
            } catch (e: Exception) {
                e.printStackTrace()
                println("❌ Gagal load JSON: ${e.message}")
                null
            }
        }
    }



}