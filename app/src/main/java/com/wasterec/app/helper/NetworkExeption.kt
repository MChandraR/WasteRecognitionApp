package com.wasterec.app.helper

import java.io.IOException

class NoConnectivityException(override val message: String) : Exception(message)