package com.example.newsapisample.di

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class NewsApp: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@NewsApp)
            androidLogger(Level.DEBUG)
            modules(listOf(viewModelScope,repositoryModule,databaseModule,apiModule,netModule))
        }
    }
}