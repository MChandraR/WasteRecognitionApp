package com.wasterec.app.model

import kotlinx.serialization.Serializable


object Destination {
    @Serializable
    sealed interface Route

    @Serializable
    data object Home : Route

    @Serializable
    data object Info : Route

    @Serializable
    data object Splash : Route

    @Serializable
    data object Training : Route

    @Serializable
    data object Annotate : Route

    @Serializable
    data object FinishTraining : Route
}

