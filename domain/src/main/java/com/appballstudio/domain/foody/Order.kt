package com.appballstudio.domain.foody

data class Order(
    val id: String,
    val state: String,
    val price: Int,
    val item: String,
    val shelf: String,
    val timestamp: Long,
    val destination: String,
)
