package com.appballstudio.samplesallday.ui.foody.orders

import androidx.lifecycle.viewModelScope
import com.appballstudio.samplesallday.R
import com.appballstudio.samplesallday.data.foody.repository.FoodyRepositoryImpl
import com.appballstudio.samplesallday.domain.foody.model.FoodyOrderDto
import com.appballstudio.samplesallday.domain.foody.model.Shelf
import com.appballstudio.samplesallday.domain.foody.repository.FoodyRepository
import com.appballstudio.samplesallday.ui.foody.theme.LightBlue
import com.appballstudio.samplesallday.ui.foody.theme.LightGray
import com.appballstudio.samplesallday.ui.foody.theme.LightGreen
import com.appballstudio.samplesallday.ui.foody.theme.LightRed
import com.appballstudio.samplesallday.ui.foody.theme.Orange
import com.appballstudio.samplesallday.utils.initTest
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

class FoodyViewModelTest : FunSpec({
    initTest()

    lateinit var viewModel: FoodyViewModelImpl
    lateinit var spyRepository: FoodyRepository

    beforeTest {
        spyRepository = spyk(FoodyRepositoryImpl(mockk()))
        viewModel = FoodyViewModelImpl(
            foodyRepository = spyRepository,
        )
    }

    test("updateOrders should emit KitchenClosed when no new orders are available") {
        coEvery { spyRepository.getOrders() } returns emptyList() // Mock empty orders

        viewModel.updateOrders()

        val expectedViewState = OrdersViewState.KitchenClosed(R.string.kitchen_closed)
        viewModel.viewState.first() shouldBe expectedViewState
    }

    test("updateOrders should update existing ones") {
        val existingOrder = mockk<FoodyOrderDto>(relaxed = true)
        val updatedOrder = mockk<FoodyOrderDto>(relaxed = true)
        val updatedOrders = listOf(updatedOrder)
        val existingOrderId = "1"

        every { updatedOrder.timestamp } returns 2
        every { existingOrder.timestamp } returns 1
        every { existingOrder.id } returns existingOrderId
        every { updatedOrder.id } returns existingOrderId
        coEvery { spyRepository.getOrders() } returns updatedOrders
        coEvery { spyRepository.getOrderById(updatedOrder.id) } returns existingOrder

        viewModel.updateOrders()

        val expectedViewState = OrdersViewState.UpdateOrders(updatedOrders)
        val actualViewStates = viewModel.viewState.take(1).toList()
        actualViewStates.firstOrNull() shouldBe expectedViewState
    }

//    test("updateOrders should remove terminated state orders") {
//        val existingOrder = mockk<FoodyOrderDto>(relaxed = true)
//        val updatedOrder = mockk<FoodyOrderDto>(relaxed = true)
//        val updatedOrders = listOf(updatedOrder)
//        val newState = "TRASHED"
//        val existingOrderId = "1"
//
//        every { updatedOrder.state } returns newState
//        every { updatedOrder.timestamp } returns 2
//        every { existingOrder.timestamp } returns 1
//        every { existingOrder.id } returns existingOrderId
//        every { updatedOrder.id } returns existingOrderId
//        coEvery { spyRepository.getOrders() } returns updatedOrders
//        coEvery { spyRepository.getOrderById(existingOrderId) } returns existingOrder
//
//        viewModel.updateOrders()
//
//        val expectedViewState = OrdersViewState.UpdateOrders(listOf())
//        val actualViewStates = viewModel.viewState.take(1).toList()
//        actualViewStates.firstOrNull() shouldBe expectedViewState
//    }

    test("updateOrders should add new ones") {
        val existingOrder = null
        val updatedOrder = mockk<FoodyOrderDto>()
        val updatedOrders = listOf(updatedOrder)

        every { updatedOrder.id } returns "1"
        coEvery { spyRepository.getOrders() } returns updatedOrders
        coEvery { spyRepository.getOrderById(updatedOrder.id) } returns existingOrder

        viewModel.updateOrders()

        val expectedViewState = OrdersViewState.UpdateOrders(updatedOrders)
        val actualViewStates = viewModel.viewState.take(1).toList()
        actualViewStates.firstOrNull() shouldBe expectedViewState
    }

    test("updateOrders should emit Error when an exception occurs") {
        val exception = Exception("Network error")
        coEvery { spyRepository.getOrders() } throws exception
        viewModel.updateOrders()

        val expectedViewState = OrdersViewState.Error(R.string.update_orders_failed)
        viewModel.viewState.first() shouldBe expectedViewState
    }

    test("onOrderClick should emit NavigateToOrderDetails event") {
        val testOrderId = "order123"
        val events = mutableListOf<Event>()
        val job = eventJob(viewModel, events)
        val mockOrder = mockk<FoodyOrderDto>(relaxed = true)
        every { mockOrder.id } returns testOrderId

        viewModel.onOrderClick(mockOrder)
        job.cancel() // Cancel the collection job to avoid blocking the test

        events shouldBe listOf(Event.NavigateToOrderDetails(testOrderId))
    }

    test("dispose should emit Dispose event") {
        val events = mutableListOf<Event>()
        val job = eventJob(viewModel, events)

        viewModel.dispose()
        job.cancel()

        events shouldBe listOf(Event.Dispose)
    }

    test("getOrderCardBackgroundColor should return LightRed for HOT shelf") {
        val order = FoodyOrderDto(
            id = "1",
            state = "preparing",
            price = 10,
            item = "Burger",
            shelf = Shelf.HOT.name,
            timestamp = 1678886400,
            destination = "Table 1",
            changelog = emptyList()
        )
        viewModel.getOrderCardBackgroundColor(order) shouldBe LightRed
    }

    test("getOrderCardBackgroundColor should return LightBlue for COLD shelf") {
        val order = FoodyOrderDto(
            id = "1",
            state = "preparing",
            price = 10,
            item = "Burger",
            shelf = Shelf.COLD.name,
            timestamp = 1678886400,
            destination = "Table 1",
            changelog = emptyList()
        )
        viewModel.getOrderCardBackgroundColor(order) shouldBe LightBlue
    }

    test("getOrderCardBackgroundColor should return LightGray for FROZEN shelf") {
        val order = FoodyOrderDto(
            id = "1",
            state = "preparing",
            price = 10,
            item = "Burger",
            shelf = Shelf.FROZEN.name,
            timestamp = 1678886400,
            destination = "Table 1",
            changelog = emptyList()
        )
        viewModel.getOrderCardBackgroundColor(order) shouldBe LightGray
    }

    test("getOrderCardBackgroundColor should return Orange for OVERFLOW shelf") {
        val order = FoodyOrderDto(
            id = "1",
            state = "preparing",
            price = 10,
            item = "Burger",
            shelf = Shelf.OVERFLOW.name,
            timestamp = 1678886400,
            destination = "Table 1",
            changelog = emptyList()
        )
        viewModel.getOrderCardBackgroundColor(order) shouldBe Orange
    }

    test("getOrderCardBackgroundColor should return LightGreen for NONE shelf") {
        val order = FoodyOrderDto(
            id = "1",
            state = "preparing",
            price = 10,
            item = "Burger",
            shelf = Shelf.NONE.name,
            timestamp = 1678886400,
            destination = "Table 1",
            changelog = emptyList()
        )
        viewModel.getOrderCardBackgroundColor(order) shouldBe LightGreen
    }

    test("getOrderCardBackgroundColor should return LightGreen for unknown shelf") {
        val order = FoodyOrderDto(
            id = "1",
            state = "preparing",
            price = 10,
            item = "Burger",
            shelf = "UNKNOWN",
            timestamp = 1678886400,
            destination = "Table 1",
            changelog = emptyList()
        )
        viewModel.getOrderCardBackgroundColor(order) shouldBe LightGreen
    }
})

private fun eventJob(
    viewModel: FoodyViewModelImpl,
    events: MutableList<Event>
) = viewModel.viewModelScope.launch {  // Collect events from _eventFlow in a coroutine
    viewModel.eventFlow.collect { events.add(it) }
}
