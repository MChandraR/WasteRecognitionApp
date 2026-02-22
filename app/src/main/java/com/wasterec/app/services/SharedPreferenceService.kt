package com.wasterec.app.services

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SharedPreferenceService(context : Context){
    var preferencesName = "wasterecog"
    var sharedPreferences:SharedPreferences = context.getSharedPreferences(this.preferencesName, 0)

    fun storeStringValue(key : String, value : String){
        this.sharedPreferences.edit {
            this.putString(key, value)
        }
    }

    fun getStringValue(key : String, value : String) : String?{
        return this.sharedPreferences.getString(key, "")
    }
}