package com.wasterec.app.helper

sealed class LoginAPIError(message : String) : AppError(message) {
    class NoInternet : LoginAPIError("No Internet Connection")
    class IncorrectPayload : LoginAPIError("Incorrect payload provided")
    class Unauthorized : LoginAPIError("Unauthorized login")
}