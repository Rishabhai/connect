package com.github.arekolek.phone

import android.app.Application
import timber.log.Timber
import net.gotev.uploadservice.UploadService;
import net.gotev.uploadservice.Logger;
import net.gotev.uploadservice.BuildConfig

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;

        // Set upload service debug log messages level
        Logger.setLogLevel(Logger.LogLevel.DEBUG);
    }
}
