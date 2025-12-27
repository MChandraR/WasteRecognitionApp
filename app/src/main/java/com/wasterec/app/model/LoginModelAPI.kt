package com.wasterec.app.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class LoginModelAPI(
    val username : String,
    val password : String
)