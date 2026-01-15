package com.wasterec.app.model.globalmodel

import kotlinx.serialization.Serializable

@Serializable
data class GlobalWeightModel(
    val weights : String,
    val bias : String
)
