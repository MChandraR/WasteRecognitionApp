package com.wasterec.app.model

import android.graphics.Bitmap

data class TrainingModel(
    var Input : Bitmap,
    var Label:Int,
    var Type : Array<DataTypeModel> = arrayOf(DataTypeModel.ORIGINAL)
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TrainingModel

        if (Label != other.Label) return false
        if (Input != other.Input) return false
        if (!Type.contentEquals(other.Type)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = Label
        result = 31 * result + Input.hashCode()
        result = 31 * result + Type.contentHashCode()
        return result
    }
}