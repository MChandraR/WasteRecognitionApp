package com.wasterec.app.services

import android.content.Context
import android.content.SharedPreferences

class SharedPreferenceService(context : Context){
    var preferencesName = "wasterecog"
    var sharedPreferences:SharedPreferences = context.getSharedPreferences(this.preferencesName, 0)

    fun storeStringValue(key : String, value : String){
        val sharedPreferencesEditor = this.sharedPreferences?.edit()
        sharedPreferencesEditor?.putString(key, value)
        sharedPreferencesEditor?.apply()
    }

    fun getStringValue(key : String, value : String) : String?{
        return this.sharedPreferences?.getString(key, "")
    }
}