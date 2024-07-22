package com.appballstudio.samplesallday.data.foody.remote

import com.appballstudio.samplesallday.domain.foody.model.FoodyOrder
import retrofit2.http.GET

interface FoodyApiService {
    @GET("order_events")
    suspend fun orders(): List<FoodyOrder>
}