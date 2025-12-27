package com.wasterec.app.model.globalmodel

import kotlinx.serialization.Serializable

@Serializable
data class GlobalModelInfoModel (
    val model_name : String,
    val num_parameters : Int,
    var input_size : IntArray,
    val num_classes : Int,
    val last_updated : String
)