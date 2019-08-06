package com.github.arekolek.phone

import android.app.Application
import timber.log.Timber
import net.gotev.uploadservice.UploadService;

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;
    }
}
