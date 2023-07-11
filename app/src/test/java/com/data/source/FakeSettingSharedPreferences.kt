package com.data.source

import com.example.noaa.services.sharepreferences.SettingSPInterface

class FakeSettingSharedPreferences(
    private var stringReadValue: String,
    private var floatReadValue: Float
) : SettingSPInterface {
    override fun writeStringToSettingSP(key: String, value: String) {
        stringReadValue = value
    }

    override fun readStringFromSettingSP(key: String): String {
        return stringReadValue
    }

    override fun writeFloatToSettingSP(key: String, value: Float) {
        floatReadValue = value
    }

    override fun readFloatFromSettingSP(key: String): Float {
        return floatReadValue
    }
}