package com.example.seoulclutureapp

import android.app.Application
import com.example.seoulclutureapp.data.AppContainer
import com.example.seoulclutureapp.data.DefaultAppContainer

class EventApplication:Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }

}