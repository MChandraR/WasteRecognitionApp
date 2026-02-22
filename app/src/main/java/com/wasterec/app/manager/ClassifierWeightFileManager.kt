package com.wasterec.app.manager

import android.content.Context
import com.google.gson.Gson
import com.wasterec.app.model.ClassifierWeightModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.lang.Exception

class ClassifierWeightFileManager(val context : Context) {
    val filename = "classifier_param.json"
    val gson : Gson = Gson()

    suspend fun saveClassifierParamToFile(weights : Array<FloatArray>, bias : FloatArray){
        withContext(Dispatchers.IO){
            try{
                val data = ClassifierWeightModel(weights, bias)
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

    suspend fun loadClassifierParamFromFile(): ClassifierWeightModel?{
        return withContext(Dispatchers.IO){
            try{
                val file = File(context.filesDir, filename)

                if(!file.exists()){
                    println("File classifier param json tidak ada")
                    return@withContext null
                }

                val fileReader = FileReader(file)

                val classifierParam = gson.fromJson(fileReader, ClassifierWeightModel::class.java)
                fileReader.close()

                println("✅ Sukses load model dari JSON.")

                return@withContext classifierParam

            }catch(e : Exception){
                e.printStackTrace()
                println("❌ Gagal load JSON: ${e.message}")
                return@withContext null
            }

        }
    }

}