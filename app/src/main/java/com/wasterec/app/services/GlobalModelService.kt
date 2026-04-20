package com.wasterec.app.services

import com.wasterec.app.model.globalmodel.ClassifierWeightModel
import com.wasterec.app.model.api_response.model_info.GlobalModelInfoModel
import com.wasterec.app.model.globalmodel.GlobalWeightModel
import com.wasterec.app.model.api_response.ResponseBody as ResponsesBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Streaming

interface GlobalModelService {
    @GET("model/info")
    suspend fun getModelInfo():Response<ResponsesBody<GlobalModelInfoModel>>

    @GET("model/classifier/weight")
    suspend fun getClassifierModelWeight():Response<ResponsesBody<ClassifierWeightModel>>

    @POST("model/weight")
    suspend fun updateModelWeight(@Body globalWeight : GlobalWeightModel):Response<GlobalModelInfoModel>

    @Streaming
    @GET("model/download?model=global")
    suspend fun downloadGlobalModelStream(): Response<ResponseBody>

    @Streaming
    @GET("model/download?model=backbone")
    suspend fun downloadBackboneModelStream(): Response<ResponseBody>

}