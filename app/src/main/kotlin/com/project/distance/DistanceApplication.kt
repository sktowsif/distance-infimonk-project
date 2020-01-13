package com.project.distance

import android.app.Application
import com.project.distance.di.apiModule
import com.project.distance.di.repositoryModule
import com.project.distance.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class DistanceApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@DistanceApplication)
            modules(listOf(apiModule, repositoryModule, viewModelModule))
        }
    }

}