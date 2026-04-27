package com.wasterec.app.model.api_response.training

data class TrainingDataResponse(
    val session_id : String,
    val user_id : String,
    val weight_id : String,
    val num_data : Int,
    val label_count : List<Int>,
    val created_at : Long,
    val status : String,
    val loss : List<Float>,
    val last_loss : Float
)
