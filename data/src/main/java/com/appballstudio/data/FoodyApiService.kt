package com.appballstudio.data

import retrofit2.Response
import retrofit2.http.GET

interface FoodyApiService {
    @GET("/order_events")
    suspend fun fetchData(): Response<Any>
}