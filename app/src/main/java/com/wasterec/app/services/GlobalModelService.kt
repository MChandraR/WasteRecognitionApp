package com.wasterec.app.services

import com.wasterec.app.model.globalmodel.GlobalModelInfoModel
import com.wasterec.app.model.globalmodel.GlobalModelWeightModel
import retrofit2.Response
import retrofit2.http.GET

interface GlobalModelService {
    @GET("model/info")
    suspend fun getModelInfo():Response<GlobalModelInfoModel>

    @GET("model/weight")
    suspend fun getModelWeight():Response<GlobalModelWeightModel>
}