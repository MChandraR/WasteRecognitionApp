package com.wasterec.app.model.globalmodel

import kotlinx.serialization.Serializable

@Serializable
data class GlobalWeightModel(
    val num_sample : Int,
    val label_count  : IntArray,
    val weights : String,
    val bias : String,
    val training_loss : List<Float>,
    val init_loss : Float,
    val final_loss : Float,
    val training_time : Long,
    val memory_usage : List<Long>,
    val energy_usage : List<Long>,
    val init_accuracy : Float,
    val final_accuracy : Float,
    val local_accuracy : Float,
    val learning_rate : Float,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GlobalWeightModel

        if (num_sample != other.num_sample) return false
        if (!label_count.contentEquals(other.label_count)) return false
        if (weights != other.weights) return false
        if (bias != other.bias) return false

        return true
    }

    override fun hashCode(): Int {
        var result = num_sample.hashCode()
        result = 31 * result + label_count.contentHashCode()
        result = 31 * result + weights.hashCode()
        result = 31 * result + bias.hashCode()
        return result
    }
}
