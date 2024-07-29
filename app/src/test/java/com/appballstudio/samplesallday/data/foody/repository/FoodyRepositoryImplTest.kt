package com.appballstudio.samplesallday.data.foody.repository

import com.appballstudio.samplesallday.data.foody.remote.FoodyApiService
import com.appballstudio.samplesallday.domain.foody.model.FoodyOrderDto
import com.appballstudio.samplesallday.domain.foody.model.Shelf
import com.appballstudio.samplesallday.domain.foody.model.State
import com.appballstudio.samplesallday.utils.initTest
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk

class FoodyRepositoryImplTest : FunSpec({
    initTest()

    lateinit var repository: FoodyRepositoryImpl
    lateinit var mockFoodyApiService: FoodyApiService

    beforeTest {
        mockFoodyApiService = mockk<FoodyApiService>()
        repository = FoodyRepositoryImpl(
            foodyApiService = mockFoodyApiService
        )
    }

    test("getOrders should return orders when successful") {
        val mockOrder1 = mockk<FoodyOrderDto>()
        val mockOrder2 = mockk<FoodyOrderDto>()
        val expectedOrders = listOf(mockOrder1, mockOrder2)
        coEvery { mockFoodyApiService.orders() } returns expectedOrders

        val actualOrders = repository.getOrders()
        actualOrders shouldBe expectedOrders
    }

    test("getOrders should return null when unsuccessful") {
        coEvery { mockFoodyApiService.orders() } throws Exception("Network error")

        val actualOrders = repository.getOrders()

        actualOrders shouldBe null
    }

    test("getOrderById should return the correct order when it exists") {
        val orderId = "orderId"
        val mockOrder = mockk<FoodyOrderDto>()

        repository.setOrderById(orderId, mockOrder)
        val retrievedOrder = repository.getOrderById(orderId)

        retrievedOrder shouldBe mockOrder
    }

    test("getOrderById should return null when the order does not exist") {
        val nonExistingOrderId = "nonExistingOrderId"
        val retrievedOrder = repository.getOrderById(nonExistingOrderId)
        retrievedOrder shouldBe null
    }

    test("setOrderById should add a new order to the repository") {
        val orderId = "orderId"
        val mockOrder = mockk<FoodyOrderDto>()

        repository.setOrderById(orderId, mockOrder)

        val retrievedOrder = repository.getOrderById(orderId)
        retrievedOrder shouldBe mockOrder
    }

    test("setOrderById should replace an existing order with the same ID") {
        // Arrange
        val orderId = "orderId"
        val order = FoodyOrderDto(
            id = orderId,
            state = State.CREATED.name,
            price = 10,
            item = "Burger",
            shelf = Shelf.HOT.name,
            timestamp = 1678886400,
            destination = "Table 1",
            changelog = emptyList()
        )

        repository.setOrderById(orderId, order)
        val updatedOrder = order.copy(state = State.COOKING.name)
        repository.setOrderById(orderId, updatedOrder)

        val retrievedOrder = repository.getOrderById(orderId)
        retrievedOrder shouldBe updatedOrder
    }

    test("removeOrderById should remove an existing order from the repository") {
        val orderId = "orderId"
        val order = mockk<FoodyOrderDto>()

        repository.setOrderById(orderId, order)
        repository.removeOrderById(orderId)

        val retrievedOrder = repository.getOrderById(orderId)
        retrievedOrder shouldBe null // Verify the order is no longer present
    }

    test("removeOrderById should have no effect if the order does not exist") {
        val nonExistingOrderId = "nonExistingOrderId"

        repository.removeOrderById(nonExistingOrderId)

        val retrievedOrder = repository.getOrderById(nonExistingOrderId)
        retrievedOrder shouldBe null // Verify the order is no longer present
    }
})