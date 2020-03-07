package com.arief.wuenak.sharedpreference

import android.content.Context
import android.content.SharedPreferences
import com.arief.wuenak.static.StaticConfiguration

class IntroPreference (context: Context) {

    private val preferences: SharedPreferences

    companion object{
        private const val PREFERENCE_NAME = StaticConfiguration.configuration
        private const val FIRST_TIME = StaticConfiguration.firstrun
    }

    init {
        preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
    }

    fun isFirstRun() = preferences.getBoolean(FIRST_TIME, true)

    fun setFirstRun(){
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putBoolean(FIRST_TIME, false)
        editor.apply()
    }
}