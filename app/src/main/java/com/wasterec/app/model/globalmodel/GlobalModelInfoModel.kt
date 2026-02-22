package com.wasterec.app.model.globalmodel

import kotlinx.serialization.Serializable

@Serializable
data class GlobalModelInfoModel (
    val id : String,
    val model_name : String,
    val model_version : String,
    var last_updated : String,
)