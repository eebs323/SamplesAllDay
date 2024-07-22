package com.appballstudio.samplesallday.domain.foody.repository

import com.appballstudio.samplesallday.domain.foody.model.FoodyOrder

interface FoodyRepository {
    suspend fun getOrders(): List<FoodyOrder>?
}