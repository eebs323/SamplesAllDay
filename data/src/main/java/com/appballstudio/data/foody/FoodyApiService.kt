package com.appballstudio.data.foody

import com.appballstudio.domain.foody.FoodyOrder
import retrofit2.http.GET

interface FoodyApiService {
    @GET("order_events")
    suspend fun orders(): List<FoodyOrder>
}