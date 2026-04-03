package com.wasterec.app.services

import com.wasterec.app.model.api_response.ResponseBody
import com.wasterec.app.model.api_response.dataset.DatasetUploadResponse
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface DatasetService {

    @Multipart
    @POST("dataset")
    fun datasetUpload(@Part file: MultipartBody.Part): Call<ResponseBody<DatasetUploadResponse>>
}