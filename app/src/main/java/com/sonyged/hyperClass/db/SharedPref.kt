package com.sonyged.hyperClass.db

import android.content.Context
import android.content.SharedPreferences
import com.sonyged.hyperClass.constants.KEY_LOGIN
import com.sonyged.hyperClass.constants.KEY_TEACHER
import com.sonyged.hyperClass.constants.KEY_TOKEN

class SharedPref(context: Context) {

    private val sharedPref: SharedPreferences = context.getSharedPreferences(
        context.packageName + "_preferences",
        Context.MODE_PRIVATE
    )

    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: SharedPref? = null

        fun getInstance(context: Context) =
            instance ?: synchronized(this) {
                instance ?: SharedPref(context).also { instance = it }
            }
    }

    fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        sharedPref.registerOnSharedPreferenceChangeListener(listener)
    }

    fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        sharedPref.unregisterOnSharedPreferenceChangeListener(listener)
    }

    fun setToken(token: String) {
        sharedPref.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(): String {
        return sharedPref.getString(KEY_TOKEN, "") ?: ""
    }

    fun setLoginSuccess(value: Boolean) {
        sharedPref.edit().putBoolean(KEY_LOGIN, value).apply()
    }

    fun isLoginSuccess(): Boolean {
        return sharedPref.getBoolean(KEY_LOGIN, false)
    }

    fun setTeacher(isTeacher: Boolean) {
        sharedPref.edit().putBoolean(KEY_TEACHER, isTeacher).apply()
    }

    fun isTeacher(): Boolean {
        return sharedPref.getBoolean(KEY_TEACHER, false)
    }


}