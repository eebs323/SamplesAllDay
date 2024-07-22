package com.appballstudio.samplesallday.data.foody

import android.util.Log
import com.appballstudio.samplesallday.domain.FoodyOrder
import com.appballstudio.samplesallday.domain.mockFoodyOrders
import com.appballstudio.samplesallday.extensions.TAG

interface FoodyRepository {
    suspend fun getOrders(): List<FoodyOrder>
}

class FoodyRepositoryImpl(private val foodyApiService: FoodyApiService) : FoodyRepository {

    override suspend fun getOrders(): List<FoodyOrder> {
        return try {
//            foodyApiService.orders()
            mockFoodyOrders
        } catch (e: Exception) {
            Log.e(TAG, "getOrders failed", e)
            listOf()
        }
    }
}