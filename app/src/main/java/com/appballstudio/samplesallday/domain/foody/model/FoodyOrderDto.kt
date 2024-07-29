package com.appballstudio.samplesallday.domain.foody.model

data class FoodyOrderDto(
    val id: String,
    val state: String,
    val price: Int,
    val item: String,
    val shelf: String,
    val timestamp: Long,
    val destination: String,
    val changelog: List<ChangelogEntry>? = emptyList()
)

data class ChangelogEntry(
    val timestamp: Long,
    val changeType: String,
    val oldValue: String,
    val newValue: String
)

enum class Shelf {
    HOT, COLD, FROZEN, OVERFLOW, NONE
}

enum class State {
    CREATED, COOKING, WAITING, DELIVERED, TRASHED, CANCELLED
}