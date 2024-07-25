package com.appballstudio.samplesallday.ui.foody.orders

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.appballstudio.samplesallday.R
import com.appballstudio.samplesallday.domain.foody.model.FoodyOrderDto
import com.appballstudio.samplesallday.domain.foody.model.Shelf
import com.appballstudio.samplesallday.domain.foody.repository.FoodyRepository
import com.appballstudio.samplesallday.ui.foody.theme.LightBlue
import com.appballstudio.samplesallday.ui.foody.theme.LightGray
import com.appballstudio.samplesallday.ui.foody.theme.LightGreen
import com.appballstudio.samplesallday.ui.foody.theme.LightRed
import com.appballstudio.samplesallday.ui.foody.theme.Orange
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class FoodyViewModelTest : FunSpec({
    mockkStatic(Log::class)
    every { Log.v(any(), any()) } returns 0
    every { Log.d(any(), any()) } returns 0
    every { Log.i(any(), any()) } returns 0
    every { Log.e(any(), any()) } returns 0
    every { Log.e(any(), any(), any()) } returns 0

    listeners(object : TestListener {
        override suspend fun beforeTest(testCase: TestCase) {
            Dispatchers.setMain(UnconfinedTestDispatcher())
        }

        override suspend fun afterTest(testCase: TestCase, result: TestResult) {
            Dispatchers.resetMain()
        }
    })

    val mockRepository = mockk<FoodyRepository>()
    val viewModel = FoodyViewModelImpl(mockRepository)

    test("updateOrders should emit KitchenClosed when no new orders are available") {
        coEvery { mockRepository.getOrders() } returns emptyList() // Mock empty orders

        viewModel.updateOrders()

        val expectedViewState = OrdersViewState.KitchenClosed(R.string.kitchen_closed)
        viewModel.viewState.first() shouldBe expectedViewState
    }

    test("updateOrders should add new orders and update existing ones") {
        val existingOrders = listOf(FoodyOrderDto("1", "preparing", 10, "Burger", "A", 1678886400, "Table 1", emptyList()))
        val newOrders = listOf(
            FoodyOrderDto("2", "ready", 12, "Pizza", "B", 1678972800, "Table 2", emptyList()),
            FoodyOrderDto("1", "delivered", 10, "Burger", "A", 1678972800, "Table 1", emptyList())
        )

        viewModel.updateOrders(existingOrders)

        coEvery { mockRepository.getOrders() } returns newOrders
        viewModel.updateOrders()

        val expectedOrders = listOf(
            FoodyOrderDto("1", "delivered", 10, "Burger", "A", 1678972800, "Table 1", emptyList()),
            FoodyOrderDto("2", "ready", 12, "Pizza", "B", 1678972800, "Table 2", emptyList())
        )
        val expectedViewState = OrdersViewState.UpdateOrders(expectedOrders)
        val actualViewStates = viewModel.viewState.take(1).toList()
        actualViewStates.firstOrNull() shouldBe expectedViewState
    }

    test("updateOrders should emit Error when an exception occurs") {
        val exception = Exception("Network error")
        coEvery { mockRepository.getOrders() } throws exception
        viewModel.updateOrders()

        val expectedViewState = OrdersViewState.Error(R.string.update_orders_failed)
        viewModel.viewState.first() shouldBe expectedViewState
    }

    test("onOrderClick should emit NavigateToOrderDetails event") {
        val testOrderId = "order123"
        val events = mutableListOf<Event>()
        val job = eventJob(viewModel, events)

        viewModel.onOrderClick(testOrderId)
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
        val order = FoodyOrderDto(id = "1", state = "preparing", price = 10, item = "Burger", shelf = Shelf.HOT.name, timestamp = 1678886400, destination = "Table 1", changelog = emptyList())
        viewModel.getOrderCardBackgroundColor(order) shouldBe LightRed
    }

    test("getOrderCardBackgroundColor should return LightBlue for COLD shelf") {
        val order = FoodyOrderDto(id = "1", state = "preparing", price = 10, item = "Burger", shelf = Shelf.COLD.name, timestamp = 1678886400, destination = "Table 1", changelog = emptyList())
        viewModel.getOrderCardBackgroundColor(order) shouldBe LightBlue
    }

    test("getOrderCardBackgroundColor should return LightGray for FROZEN shelf") {
        val order = FoodyOrderDto(id = "1", state = "preparing", price = 10, item = "Burger", shelf = Shelf.FROZEN.name, timestamp = 1678886400, destination = "Table 1", changelog = emptyList())
        viewModel.getOrderCardBackgroundColor(order) shouldBe LightGray
    }

    test("getOrderCardBackgroundColor should return Orange for OVERFLOW shelf") {
        val order = FoodyOrderDto(id = "1", state = "preparing", price = 10, item = "Burger", shelf = Shelf.OVERFLOW.name, timestamp = 1678886400, destination = "Table 1", changelog = emptyList())
        viewModel.getOrderCardBackgroundColor(order) shouldBe Orange
    }

    test("getOrderCardBackgroundColor should return LightGreen for NONE shelf") {
        val order = FoodyOrderDto(id = "1", state = "preparing", price = 10, item = "Burger", shelf = Shelf.NONE.name, timestamp = 1678886400, destination = "Table 1", changelog = emptyList())
        viewModel.getOrderCardBackgroundColor(order) shouldBe LightGreen
    }

    test("getOrderCardBackgroundColor should return LightGreen for unknown shelf") {
        val order = FoodyOrderDto(id = "1", state = "preparing", price = 10, item = "Burger", shelf = "UNKNOWN", timestamp = 1678886400, destination = "Table 1", changelog = emptyList())
        viewModel.getOrderCardBackgroundColor(order) shouldBe LightGreen
    }
})

private fun eventJob(
    viewModel: FoodyViewModelImpl,
    events: MutableList<Event>
) = viewModel.viewModelScope.launch {  // Collect events from _eventFlow in a coroutine
    viewModel.eventFlow.collect { events.add(it) }
}