package com.example.aptikma_remake

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import io.paperdb.Paper

@HiltAndroidApp
class MyApplication: Application(){
    override fun onCreate() {
        super.onCreate()
        Paper.init(this)
    }
}