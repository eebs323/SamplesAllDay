package com.appballstudio.data.foody

import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val FOODY_RETROFIT = "FOODY_RETROFIT"
const val FOODY_BASE_URL = "http://localhost:8080/"

val dataModule = module {
    single(named(FOODY_RETROFIT)) {
        Retrofit.Builder()
            .baseUrl(FOODY_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}