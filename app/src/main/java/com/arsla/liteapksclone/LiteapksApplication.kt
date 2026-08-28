package com.arsla.liteapksclone

import android.app.Application
import androidx.work.Configuration
import com.arsla.liteapksclone.error.CrashHandler
import com.arsla.liteapksclone.error.ErrorHandler
import androidx.hilt.work.HiltWorkerFactory
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class LiteapksApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        ErrorHandler.initialize(this)
        CrashHandler.initialize()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
