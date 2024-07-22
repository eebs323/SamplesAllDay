package com.appballstudio.samplesallday

import android.app.Application
import com.appballstudio.samplesallday.data.foody.foodyDataModule
import com.appballstudio.samplesallday.ui.foody.foodyUiModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class SamplesAllDayApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
//            androidLogger()
            androidContext(this@SamplesAllDayApplication)
            modules(
                foodyDataModule,
                foodyUiModule
            )
        }
    }
}