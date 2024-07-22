package com.appballstudio.samplesallday.data.foody.repository

import android.util.Log
import com.appballstudio.samplesallday.data.foody.remote.FoodyApiService
import com.appballstudio.samplesallday.domain.foody.model.FoodyOrder
import com.appballstudio.samplesallday.domain.foody.repository.FoodyRepository
import com.appballstudio.samplesallday.extensions.TAG
import kotlinx.coroutines.delay

class FoodyRepositoryImpl(private val foodyApiService: FoodyApiService) : FoodyRepository {

    override suspend fun getOrders(): List<FoodyOrder>? {
        return try {
            delay(1000)
            foodyApiService.orders()
        } catch (e: Exception) {
            Log.e(TAG, "getOrders failed", e)
            null
        }
    }
}