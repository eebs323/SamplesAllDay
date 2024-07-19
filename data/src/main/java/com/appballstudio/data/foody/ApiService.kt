package com.appballstudio.data.foody

import com.appballstudio.domain.foody.Orders
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("/order_events")
    suspend fun orders(): Response<Orders>
}