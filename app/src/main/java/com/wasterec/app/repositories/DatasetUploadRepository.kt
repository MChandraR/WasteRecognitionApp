package com.wasterec.app.repositories

import android.content.Context
import com.wasterec.app.helper.InternalServerErrorException
import com.wasterec.app.helper.NoConnectivityException
import com.wasterec.app.model.api_response.ResponseBody
import com.wasterec.app.model.api_response.dataset.DatasetUploadResponse
import com.wasterec.app.services.ApiService
import com.wasterec.app.services.DatasetService
import com.wasterec.app.services.SharedPreferenceService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.File

class DatasetUploadRepository(val context: Context, val onException : (e : Exception)->Unit = {}): ApiService() {
    fun getDatasetService(request : suspend(retrofit : DatasetService)->Unit){
        val authToken = SharedPreferenceService(context).getStringValue("authToken")
        authToken?.let{addAuthorizationBerer(authToken)}
        this.retrofit?.create(DatasetService::class.java)?.let{
            CoroutineScope(Dispatchers.IO).launch{
                try{
                    request(it)
                }catch(e : Exception ){
                    onException(NoConnectivityException("Tidak dapat terhubung ke server !"))
                }
            }
        }
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
    fun uploadDatasetToServer(fileToUpload : File, onSuccess : ()->Unit = {}, onFailed : ()->Unit = {} ){
//        val fileToUpload = getFileFromAssets("attribute.txt")

        val requestFile = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            fileToUpload
        )

        val requestBody = MultipartBody.Part.createFormData("file", fileToUpload.name, requestFile)

        getDatasetService{
            val response =  it.datasetUpload(requestBody)

            response.enqueue(object : Callback<ResponseBody<DatasetUploadResponse>> {
                override fun onResponse(
                    call: Call<ResponseBody<DatasetUploadResponse>?>,
                    response: Response<ResponseBody<DatasetUploadResponse>?>
                ) {
                    if(response.code()==401 || response.code() == 500){
                        onException(InternalServerErrorException("Internal server error"))
                    }else{
                        onSuccess()
                    }
                    println("Response: ${response.body()} ${response.code()} ${response.message()}")
                }

                override fun onFailure(
                    call: Call<ResponseBody<DatasetUploadResponse>?>,
                    t: Throwable
                ) {
                    onFailed()
                    onException(InternalServerErrorException("Internal server error"))
                    println("Error: ${t.message} ${t.stackTrace}")
                }

            })
        }
    }
}