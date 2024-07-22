package com.appballstudio.samplesallday.data.foody.di

import com.appballstudio.samplesallday.data.foody.remote.FoodyApiService
import com.appballstudio.samplesallday.data.foody.repository.FoodyRepositoryImpl
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val FOODY_RETROFIT = "FOODY_RETROFIT"
const val FOODY_BASE_URL = "http://10.0.2.2:8080/"

val foodyDataModule = module {
    single(named(FOODY_RETROFIT)) {
        Retrofit.Builder()
            .baseUrl(FOODY_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    single {
        val foodyRetrofit = get<Retrofit>(named(FOODY_RETROFIT))
        foodyRetrofit.create(FoodyApiService::class.java) as FoodyApiService
    }
    single {
        FoodyRepositoryImpl(foodyApiService = get<FoodyApiService>())
    }
}