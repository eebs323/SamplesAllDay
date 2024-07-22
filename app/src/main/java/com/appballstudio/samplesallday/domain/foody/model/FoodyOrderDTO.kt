package com.appballstudio.samplesallday.domain.foody.model

data class FoodyOrder(
    val id: String,
    val state: String,
    val price: Int,
    val item: String,
    val shelf: String,
    val timestamp: Long,
    val destination: String,
)

enum class Shelf {
    HOT, COLD, FROZEN, OVERFLOW, NONE
}

val mockFoodyOrders: List<FoodyOrder> = listOf(
    FoodyOrder(
        id = "id1",
        state = "CREATED",
        price = 100,
        item = "item1",
        shelf = "HOT",
        timestamp = 10000,
        destination = "destination1"
    ),
    FoodyOrder(
        id = "id2",
        state = "COOKING",
        price = 200,
        item = "item2",
        shelf = "COLD",
        timestamp = 20000,
        destination = "destination2"
    ),
    FoodyOrder(
        id = "id3",
        state = "WAITING",
        price = 300,
        item = "item3",
        shelf = "FROZEN",
        timestamp = 30000,
        destination = "destination3"
    ),
    FoodyOrder(
        id = "id4",
        state = "DELIVERED",
        price = 400,
        item = "item4",
        shelf = "OVERFLOW",
        timestamp = 40000,
        destination = "destination4"
    ),
)
