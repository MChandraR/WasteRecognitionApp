package com.wasterec.app.helper

sealed class AppError(message:String) : Exception(message)