package com.tlw.wolfshield.utils

import android.content.Context
import android.content.SharedPreferences

object LocalData {
    private const val PREFS_NAME = "com.tlw.wolfshield.cache"
    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveStringValue(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getStringValue(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    fun saveBooleanValue(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    fun getBooleanValue(key: String, defaultValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    fun clearSharedPreferences() {
        sharedPreferences.edit().clear().apply()
    }

    //=====================================================================

    fun saveUserID(uid: String) {
        saveStringValue(Constant.USER_UID, uid)
    }

    fun getUserID(): String {
        return getStringValue(Constant.USER_UID, "")
    }

    fun saveParentRole(parent: Boolean) {
        saveBooleanValue(Constant.USER_ROLE, parent)
    }

    fun getParentRole(): Boolean {
        return getBooleanValue(Constant.USER_ROLE, false)
    }
}