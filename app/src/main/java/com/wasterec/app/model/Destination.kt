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

    @Serializable
    data object Login : Route

    @Serializable
    data object Import : Route

    @Serializable
    data object Preprocess : Route

    @Serializable
    data object ImportDataset : Route

    @Serializable
    data object WeightLoading : Route

    @Serializable
    data object WeightLoadingForClassification : Route

    @Serializable
    data object TrainingHistoryDetail : Route

    @Serializable
    data object Testing : Route

    @Serializable
    data object Classify : Route
}

