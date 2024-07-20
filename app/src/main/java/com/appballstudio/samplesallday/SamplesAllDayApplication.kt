package com.appballstudio.samplesallday

import android.app.Application
import com.appballstudio.data.foody.foodyDataModule
import com.appballstudio.foody.ui.foodyUiModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class SamplesAllDayApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@SamplesAllDayApplication)
            modules(
                foodyDataModule,
                foodyUiModule
            )
        }
    }
}