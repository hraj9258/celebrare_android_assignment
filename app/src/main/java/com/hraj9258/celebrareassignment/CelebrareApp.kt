package com.hraj9258.celebrareassignment

import android.app.Application
import com.hraj9258.celebrareassignment.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class CelebrareApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@CelebrareApp)
            modules(appModule)
        }
    }
}
