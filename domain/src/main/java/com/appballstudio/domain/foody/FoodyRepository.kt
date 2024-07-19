package com.appballstudio.domain.foody

interface FoodyRepository {
    suspend fun getOrders(): List<FoodyOrder>
}