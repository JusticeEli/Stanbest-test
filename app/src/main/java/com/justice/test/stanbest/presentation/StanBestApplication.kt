package com.justice.test.stanbest.presentation

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class StanBestApplication : Application() {
    private val TAG = "NyansapoApplication"
    override fun onCreate() {
        super.onCreate()
    }
}