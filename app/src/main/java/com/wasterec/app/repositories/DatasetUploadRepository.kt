package com.wasterec.app.repositories

import android.content.Context
import com.wasterec.app.model.api_response.ResponseBody
import com.wasterec.app.model.api_response.dataset.DatasetUploadResponse
import com.wasterec.app.services.ApiService
import com.wasterec.app.services.DatasetService
import com.wasterec.app.services.SharedPreferenceService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class DatasetUploadRepository(val context: Context): ApiService() {
    fun getDatasetService():DatasetService?{
        val authToken = SharedPreferenceService(context).getStringValue("authToken")
        authToken?.let{addAuthorizationBerer(authToken)}
        return this.retrofit?.create(DatasetService::class.java)
    }
    fun getFileFromAssets(fileName: String): File {
        val tempFile = File(context.cacheDir, fileName) // Simpan di folder cache
        context.assets.open(fileName).use { inputStream ->
            tempFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return tempFile
    }
    fun uploadDatasetToServer(fileToUpload : File){
//        val fileToUpload = getFileFromAssets("attribute.txt")

        val requestFile = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            fileToUpload
        )

        val requestBody = MultipartBody.Part.createFormData("file", fileToUpload.name, requestFile)

        getDatasetService()?.let {
            val response =  it.datasetUpload(requestBody)

            response.enqueue(object : Callback<ResponseBody<DatasetUploadResponse>> {
                override fun onResponse(
                    call: Call<ResponseBody<DatasetUploadResponse>?>,
                    response: Response<ResponseBody<DatasetUploadResponse>?>
                ) {
                    println("Response: ${response.body()} ${response.code()} ${response.message()}")
                }

                override fun onFailure(
                    call: Call<ResponseBody<DatasetUploadResponse>?>,
                    t: Throwable
                ) {
                    println("Error: ${t.message} ${t.stackTrace}")
                }

            })
        }
    }
}