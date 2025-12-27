package com.wasterec.app.helper

sealed class GlobalModelError(message : String) : AppError(message) {
    class NoInternet : GlobalModelError("No Internet Connection")
    class IncorrectPayload : GlobalModelError("Incorrect payload provided")
    class Unauthorized : GlobalModelError("Unauthorized login")
}