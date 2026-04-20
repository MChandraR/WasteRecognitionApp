package com.wasterec.app.model.domain

data class TrainingData(
    val session_id : String,
    val user_id : String,
    val weight_id : String,
    val num_data : Int,
    val label_count : List<Int>,
    val created_at : String,
    val status : String,
    val loss : List<Float>,
    val average_loss : Float
)
