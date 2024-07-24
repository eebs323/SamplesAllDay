package com.appballstudio.samplesallday.domain.foody.model

data class FoodyOrderDto(
    val id: String,
    val state: String,
    val price: Int,
    val item: String,
    val shelf: String,
    val timestamp: Long,
    val destination: String,
    val changelog: List<ChangelogEntry>
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

val mockFoodyOrderDtos: List<FoodyOrderDto> = listOf(
    FoodyOrderDto(
        id = "id1",
        state = "CREATED",
        price = 100,
        item = "item1",
        shelf = "HOT",
        timestamp = 10000,
        destination = "destination1",
        changelog = listOf()
    ),
    FoodyOrderDto(
        id = "id2",
        state = "COOKING",
        price = 200,
        item = "item2",
        shelf = "COLD",
        timestamp = 20000,
        destination = "destination2",
        changelog = listOf()
    ),
    FoodyOrderDto(
        id = "id3",
        state = "WAITING",
        price = 300,
        item = "item3",
        shelf = "FROZEN",
        timestamp = 30000,
        destination = "destination3",
        changelog = listOf()
    ),
    FoodyOrderDto(
        id = "id4",
        state = "DELIVERED",
        price = 400,
        item = "item4",
        shelf = "OVERFLOW",
        timestamp = 40000,
        destination = "destination4",
        changelog = listOf()
    ),
)
