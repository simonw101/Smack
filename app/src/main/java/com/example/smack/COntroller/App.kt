package com.example.smack.COntroller

import android.app.Application
import android.content.SharedPreferences
import com.example.smack.Utilities.SharedPrefs

class App : Application(){

    companion object {

        lateinit var prefs: SharedPrefs

    }

    override fun onCreate() {

        prefs = SharedPrefs(applicationContext)

        super.onCreate()
    }

}