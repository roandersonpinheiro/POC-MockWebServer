package com.roanderson.mockwebserver.app

import android.app.Application
import com.roanderson.mockwebserver.data.di.DataModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@App)
            DataModule.load()
        }
    }
}