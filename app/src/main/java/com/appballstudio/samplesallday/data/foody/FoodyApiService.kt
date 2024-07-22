package com.appballstudio.samplesallday.data.foody

import com.appballstudio.samplesallday.domain.FoodyOrder
import retrofit2.http.GET

interface FoodyApiService {
    @GET("order_events")
    suspend fun orders(): List<FoodyOrder>
}