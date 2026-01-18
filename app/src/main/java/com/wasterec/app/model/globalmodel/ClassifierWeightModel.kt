package com.wasterec.app.model.globalmodel

import kotlinx.serialization.Serializable

@Serializable
data class ClassifierWeightModel (
    val weight : String,
    val bias : String
)