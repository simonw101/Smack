package com.example.smack.Utilities

import android.content.Context
import android.content.SharedPreferences
import com.android.volley.toolbox.Volley

class SharedPrefs(context: Context) {

    var PREFS_FILE_NAME = "prefs"

    var prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILE_NAME, 0)

    var IS_LOGGED_IN = "isLoggedIn"

    var AUTH_TOKEN = "authToken"

    var USER_EMAIL = "userEmail"

    var isLoggedIn: Boolean

        get() = prefs.getBoolean(IS_LOGGED_IN, false)

        set(value) = prefs.edit().putBoolean(IS_LOGGED_IN, value).apply()

    var authToken: String

        get() = prefs.getString(AUTH_TOKEN, "")

        set(value) = prefs.edit().putString(AUTH_TOKEN, value).apply()

    var userEmail: String

        get() = prefs.getString(USER_EMAIL, "")

        set(value) = prefs.edit().putString(USER_EMAIL, value).apply()

    val requestQueuse = Volley.newRequestQueue(context)
}