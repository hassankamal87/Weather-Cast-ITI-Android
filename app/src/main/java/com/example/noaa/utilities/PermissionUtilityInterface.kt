package com.example.noaa.utilities

import android.content.Context

interface PermissionUtilityInterface {
    fun checkPermission(context: Context): Boolean
    fun isLocationIsEnabled(context: Context): Boolean
    fun checkConnection(context: Context): Boolean
    fun notificationPermission(context: Context): Boolean
}