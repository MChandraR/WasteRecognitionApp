package com.wasterec.app.model.api_response

data class ResponseBody<BodyType>(
    val status : Int,
    val message : String,
    val data : BodyType
)