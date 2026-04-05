package com.wasterec.app.model.domain

data class TrainingData(
    val session_id : String,
    val user_id : String,
    val weight_id : String,
    val created_at : String,
    val status : String,
    val loss : List<Float>,
    val average_loss : Float
)
