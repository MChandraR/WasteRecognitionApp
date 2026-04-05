package com.wasterec.app.model.api_response.training

data class TrainingDataResponse(
    val session_id : String,
    val user_id : String,
    val weight_id : String,
    val created_at : String,
    val status : String,
    val loss : List<Float>,
    val average_loss : Float
)
