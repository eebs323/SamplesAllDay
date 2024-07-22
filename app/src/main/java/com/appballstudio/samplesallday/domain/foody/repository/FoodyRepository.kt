package com.appballstudio.samplesallday.domain.foody.repository

import com.appballstudio.samplesallday.domain.foody.model.FoodyOrderDto

interface FoodyRepository {
    suspend fun getOrders(): List<FoodyOrderDto>?
}