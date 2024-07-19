package com.appballstudio.data.foody

import android.util.Log
import com.appballstudio.data.extensions.TAG
import com.appballstudio.domain.foody.FoodyOrder
import com.appballstudio.domain.foody.FoodyRepository

class FoodyRepositoryImpl(private val foodyApiService: FoodyApiService) : FoodyRepository {
    override suspend fun getOrders(): List<FoodyOrder> {
        return try {
           foodyApiService.orders()
        } catch (e: Exception) {
            Log.e(TAG, "getOrders failed", e)
            listOf()
        }
    }
}