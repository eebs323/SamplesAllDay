package com.appballstudio.samplesallday.domain.foody.repository

import com.appballstudio.samplesallday.domain.foody.model.FoodyOrderDto

interface FoodyRepository {
    var orders: Map<String, FoodyOrderDto>

    suspend fun getOrders(): List<FoodyOrderDto>?
    fun getOrderById(orderId: String): FoodyOrderDto?
    fun setOrderById(orderId: String, newOrder: FoodyOrderDto)
    fun removeOrderById(orderId: String)
}