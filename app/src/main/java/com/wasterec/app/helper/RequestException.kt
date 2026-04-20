package com.wasterec.app.helper

class ExpiredAuthTokenException(override val message: String) : Exception(message)
class InternalServerErrorException(override val message: String) : Exception(message)