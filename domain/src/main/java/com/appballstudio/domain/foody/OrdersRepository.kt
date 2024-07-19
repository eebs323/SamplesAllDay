package com.appballstudio.domain.foody

import com.appballstudio.data.foody.ApiService

interface OrdersRepository {
    suspend fun getOrders(): List<Order>
}

class OrdersRepositoryImpl(val apiService: ApiService) : OrdersRepository {
    override suspend fun getOrders(): List<Order> {
        TODO("Not yet implemented")
    }
}