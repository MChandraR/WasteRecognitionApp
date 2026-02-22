package com.wasterec.app.model.globalmodel

import kotlinx.serialization.Serializable

@Serializable
data class ClassifierWeightModel (
    val weights : Array<FloatArray>,
    val bias : FloatArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ClassifierWeightModel

        if (!weights.contentDeepEquals(other.weights)) return false
        if (!bias.contentEquals(other.bias)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = weights.contentDeepHashCode()
        result = 31 * result + bias.contentHashCode()
        return result
    }
}