package com.appballstudio.samplesallday.data.foody.repository

import android.util.Log
import com.appballstudio.samplesallday.data.foody.remote.FoodyApiService
import com.appballstudio.samplesallday.domain.foody.model.FoodyOrderDto
import com.appballstudio.samplesallday.domain.foody.repository.FoodyRepository
import com.appballstudio.samplesallday.extensions.TAG

class FoodyRepositoryImpl(private val foodyApiService: FoodyApiService) : FoodyRepository {

    private val _orders: MutableMap<String, FoodyOrderDto> = mutableMapOf() // Store orders by ID
    override var orders: Map<String, FoodyOrderDto> = _orders

    override suspend fun getOrders(): List<FoodyOrderDto>? {
        return try {
            foodyApiService.orders()
        } catch (e: Exception) {
            Log.e(TAG, "getOrders failed", e)
            null
        }
    }

    override fun getOrderById(orderId: String): FoodyOrderDto? {
        return _orders[orderId]
    }

    override fun setOrderById(orderId: String, newOrder: FoodyOrderDto) {
        _orders[orderId] = newOrder
    }

    override fun removeOrderById(orderId: String) {
        _orders.remove(orderId)
    }
}