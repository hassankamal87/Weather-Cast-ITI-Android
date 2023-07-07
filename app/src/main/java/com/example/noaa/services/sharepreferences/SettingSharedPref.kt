package com.example.noaa.services.sharepreferences

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.example.noaa.utilities.Constants


class SettingSharedPref private constructor(context: Context) : SettingSPInterface {
    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(Constants.SETTING, AppCompatActivity.MODE_PRIVATE)
    }

    companion object {
        private var instance: SettingSharedPref? = null

        fun getInstance(context: Context): SettingSharedPref {
            return instance ?: synchronized(this) {
                instance ?: SettingSharedPref(context).also { instance = it }
            }
        }
    }

    override fun writeStringToSettingSP(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    override fun readStringFromSettingSP(key: String): String {
        var value = ""
        sharedPreferences.getString(key, "null").let {
            value = it ?: "null"
        }
        return value
    }


    override fun writeFloatToSettingSP(key: String, value: Float) {
        sharedPreferences.edit().putFloat(key, value).apply()
    }

    override fun readFloatFromSettingSP(key: String): Float {
        var value = 0f
        sharedPreferences.getFloat(key, 0f).let {
            value = it
        }
        return value
    }
}