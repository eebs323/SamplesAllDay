package com.appballstudio.samplesallday

import android.app.Application
import com.appballstudio.samplesallday.data.foody.di.foodyDataModule
import com.appballstudio.samplesallday.ui.foody.di.foodyUiModule
import org.koin.android.ext.koin.androidContext
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