package com.appballstudio.samplesallday.data.foody.remote

import com.appballstudio.samplesallday.domain.foody.model.FoodyOrderDto
import retrofit2.http.GET

interface FoodyApiService {
    @GET("order_events")
    suspend fun orders(): List<FoodyOrderDto>
}