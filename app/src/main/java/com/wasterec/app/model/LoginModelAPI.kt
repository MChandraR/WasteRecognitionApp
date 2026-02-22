package com.wasterec.app.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginModelAPI(
    val username : String,
    val password : String
)