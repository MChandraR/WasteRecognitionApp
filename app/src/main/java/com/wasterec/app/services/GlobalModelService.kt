package com.wasterec.app.services

import com.wasterec.app.model.globalmodel.GlobalModelInfoModel
import com.wasterec.app.model.globalmodel.GlobalModelWeightModel
import com.wasterec.app.model.globalmodel.GlobalWeightModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface GlobalModelService {
    @GET("model/info")
    suspend fun getModelInfo():Response<GlobalModelInfoModel>

    @GET("model/weight")
    suspend fun getModelWeight():Response<GlobalModelWeightModel>

    @POST("model/weight")
    suspend fun updateModelWeight(@Body globalWeight : GlobalWeightModel):Response<GlobalModelInfoModel>
}