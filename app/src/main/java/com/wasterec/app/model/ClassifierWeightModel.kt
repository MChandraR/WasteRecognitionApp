package com.wasterec.app.model

import com.google.gson.annotations.SerializedName

data class ClassifierWeightModel(
    @SerializedName("weights")
    val weights : Array<FloatArray>,

    @SerializedName("bias")
    val bias : FloatArray
){


    override fun hashCode(): Int {
        var result = weights.contentDeepHashCode()
        result = 31 * result + bias.contentHashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ClassifierWeightModel

        if (!weights.contentDeepEquals(other.weights)) return false
        if (!bias.contentEquals(other.bias)) return false

        return true
    }
}
