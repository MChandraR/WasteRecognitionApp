package com.wasterec.app.model.api_request

import kotlinx.serialization.Serializable

@Serializable
data class LoginModelRequestAPI(
    val username : String,
    val password : String
)