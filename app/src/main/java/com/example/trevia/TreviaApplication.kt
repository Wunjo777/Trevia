package com.example.trevia

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TreviaApplication : Application()
{
    override fun onCreate()
    {
        super.onCreate()
    }
}