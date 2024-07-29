package com.appballstudio.samplesallday.ui.foody.orders

import androidx.lifecycle.viewModelScope
import com.appballstudio.samplesallday.R
import com.appballstudio.samplesallday.data.foody.repository.FoodyRepositoryImpl
import com.appballstudio.samplesallday.domain.foody.model.ChangelogEntry
import com.appballstudio.samplesallday.domain.foody.model.FoodyOrderDto
import com.appballstudio.samplesallday.domain.foody.model.Shelf
import com.appballstudio.samplesallday.domain.foody.model.State
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

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

    test("updateOrders should update existing ones") {
        val existingOrderId = "1"
        val existingOrderState = State.CREATED.name
        val existingOrderPrice = 10
        val existingOrderItem = "Burger"
        val existingOrderShelf = Shelf.COLD.name
        val existingOrderTimestamp = 1L
        val existingOrderDestination = "Table 1"
        val existingOrder = FoodyOrderDto(
            id = existingOrderId,
            state = existingOrderState,
            price = existingOrderPrice,
            item = existingOrderItem,
            shelf = existingOrderShelf,
            timestamp = existingOrderTimestamp,
            destination = existingOrderDestination
        )

        val updatedOrderId = "1"
        val updatedOrderState = State.COOKING.name
        val updatedOrderPrice = 10
        val updatedOrderItem = "Burger"
        val updatedOrderShelf = Shelf.HOT.name
        val updatedOrderTimestamp = 2L
        val updatedOrderDestination = "Table 1"
        val updatedOrder = FoodyOrderDto(
            id = updatedOrderId,
            state = updatedOrderState,
            price = updatedOrderPrice,
            item = updatedOrderItem,
            shelf = updatedOrderShelf,
            timestamp = updatedOrderTimestamp,
            destination = updatedOrderDestination,
            changelog = mutableListOf(
                ChangelogEntry(
                    timestamp = updatedOrderTimestamp,
                    changeType = "State Change",
                    oldValue = existingOrderState,
                    newValue = updatedOrderState
                ),
                ChangelogEntry(
                    timestamp = updatedOrderTimestamp,
                    changeType = "Shelf Change",
                    oldValue = existingOrderShelf,
                    newValue = updatedOrderShelf
                )
            )
        )

        val updatedOrders = listOf(updatedOrder)

        coEvery { spyRepository.getOrders() } returns updatedOrders
        coEvery { spyRepository.getOrderById(existingOrderId) } returns existingOrder

        viewModel.updateOrders()

        val expectedViewState = OrdersViewState.UpdateOrders(updatedOrders)
        val actualViewStates = viewModel.viewState.take(1).toList()
        actualViewStates.firstOrNull() shouldBe expectedViewState
    }

    test("updateOrders should remove TRASHED terminated state order") {
        val existingOrderId = "1"
        val existingOrderState = State.CREATED.name
        val existingOrderPrice = 10
        val existingOrderItem = "Burger"
        val existingOrderShelf = Shelf.COLD.name
        val existingOrderTimestamp = 1L
        val existingOrderDestination = "Table 1"
        val existingOrder = FoodyOrderDto(
            id = existingOrderId,
            state = existingOrderState,
            price = existingOrderPrice,
            item = existingOrderItem,
            shelf = existingOrderShelf,
            timestamp = existingOrderTimestamp,
            destination = existingOrderDestination
        )

        val updatedOrderId = "1"
        val updatedOrderState = State.TRASHED.name
        val updatedOrderPrice = 10
        val updatedOrderItem = "Burger"
        val updatedOrderShelf = Shelf.HOT.name
        val updatedOrderTimestamp = 2L
        val updatedOrderDestination = "Table 1"
        val updatedOrder = FoodyOrderDto(
            id = updatedOrderId,
            state = updatedOrderState,
            price = updatedOrderPrice,
            item = updatedOrderItem,
            shelf = updatedOrderShelf,
            timestamp = updatedOrderTimestamp,
            destination = updatedOrderDestination,
            changelog = mutableListOf(
                ChangelogEntry(
                    timestamp = updatedOrderTimestamp,
                    changeType = "State Change",
                    oldValue = existingOrderState,
                    newValue = updatedOrderState
                ),
                ChangelogEntry(
                    timestamp = updatedOrderTimestamp,
                    changeType = "Shelf Change",
                    oldValue = existingOrderShelf,
                    newValue = updatedOrderShelf
                )
            )
        )
        val updatedOrders = listOf(updatedOrder)

        coEvery { spyRepository.getOrders() } returns updatedOrders
        coEvery { spyRepository.getOrderById(existingOrderId) } returns existingOrder

        viewModel.updateOrders()

        val expectedViewState = OrdersViewState.UpdateOrders(listOf())
        val actualViewStates = viewModel.viewState.take(1).toList()
        actualViewStates.firstOrNull() shouldBe expectedViewState
        viewModel.numOrdersTrashed shouldBe 1
        viewModel.totalWaste shouldBe NumberFormat.getNumberInstance(Locale.US).format(updatedOrderPrice)
        viewModel.numOrdersDelivered shouldBe 0
        viewModel.totalSales shouldBe "0"
        NumberFormat.getNumberInstance(Locale.US).format(viewModel.totalSales.toInt() - viewModel.totalWaste.toInt())
    }

    test("updateOrders should remove DELIVERED terminated state order") {
        val existingOrderId = "1"
        val existingOrderState = State.CREATED.name
        val existingOrderPrice = 10
        val existingOrderItem = "Burger"
        val existingOrderShelf = Shelf.COLD.name
        val existingOrderTimestamp = 1L
        val existingOrderDestination = "Table 1"
        val existingOrder = FoodyOrderDto(
            id = existingOrderId,
            state = existingOrderState,
            price = existingOrderPrice,
            item = existingOrderItem,
            shelf = existingOrderShelf,
            timestamp = existingOrderTimestamp,
            destination = existingOrderDestination
        )

        val updatedOrderId = "1"
        val updatedOrderState = State.DELIVERED.name
        val updatedOrderPrice = 10
        val updatedOrderItem = "Burger"
        val updatedOrderShelf = Shelf.HOT.name
        val updatedOrderTimestamp = 2L
        val updatedOrderDestination = "Table 1"
        val updatedOrder = FoodyOrderDto(
            id = updatedOrderId,
            state = updatedOrderState,
            price = updatedOrderPrice,
            item = updatedOrderItem,
            shelf = updatedOrderShelf,
            timestamp = updatedOrderTimestamp,
            destination = updatedOrderDestination,
            changelog = mutableListOf(
                ChangelogEntry(
                    timestamp = updatedOrderTimestamp,
                    changeType = "State Change",
                    oldValue = existingOrderState,
                    newValue = updatedOrderState
                ),
                ChangelogEntry(
                    timestamp = updatedOrderTimestamp,
                    changeType = "Shelf Change",
                    oldValue = existingOrderShelf,
                    newValue = updatedOrderShelf
                )
            )
        )
        val updatedOrders = listOf(updatedOrder)

        coEvery { spyRepository.getOrders() } returns updatedOrders
        coEvery { spyRepository.getOrderById(existingOrderId) } returns existingOrder

        viewModel.updateOrders()

        val expectedViewState = OrdersViewState.UpdateOrders(listOf())
        val actualViewStates = viewModel.viewState.take(1).toList()
        actualViewStates.firstOrNull() shouldBe expectedViewState
        viewModel.numOrdersTrashed shouldBe 0
        viewModel.totalWaste shouldBe "0"
        viewModel.numOrdersDelivered shouldBe 1
        viewModel.totalSales shouldBe NumberFormat.getNumberInstance(Locale.US).format(updatedOrderPrice)
        NumberFormat.getNumberInstance(Locale.US).format(viewModel.totalSales.toInt() - viewModel.totalWaste.toInt())
    }

    test("updateOrders should remove CANCELLED terminated state order") {
        val existingOrderId = "1"
        val existingOrderState = State.CREATED.name
        val existingOrderPrice = 10
        val existingOrderItem = "Burger"
        val existingOrderShelf = Shelf.COLD.name
        val existingOrderTimestamp = 1L
        val existingOrderDestination = "Table 1"
        val existingOrder = FoodyOrderDto(
            id = existingOrderId,
            state = existingOrderState,
            price = existingOrderPrice,
            item = existingOrderItem,
            shelf = existingOrderShelf,
            timestamp = existingOrderTimestamp,
            destination = existingOrderDestination
        )

        val updatedOrderId = "1"
        val updatedOrderState = State.CANCELLED.name
        val updatedOrderPrice = 10
        val updatedOrderItem = "Burger"
        val updatedOrderShelf = Shelf.HOT.name
        val updatedOrderTimestamp = 2L
        val updatedOrderDestination = "Table 1"
        val updatedOrder = FoodyOrderDto(
            id = updatedOrderId,
            state = updatedOrderState,
            price = updatedOrderPrice,
            item = updatedOrderItem,
            shelf = updatedOrderShelf,
            timestamp = updatedOrderTimestamp,
            destination = updatedOrderDestination,
            changelog = mutableListOf(
                ChangelogEntry(
                    timestamp = updatedOrderTimestamp,
                    changeType = "State Change",
                    oldValue = existingOrderState,
                    newValue = updatedOrderState
                ),
                ChangelogEntry(
                    timestamp = updatedOrderTimestamp,
                    changeType = "Shelf Change",
                    oldValue = existingOrderShelf,
                    newValue = updatedOrderShelf
                )
            )
        )
        val updatedOrders = listOf(updatedOrder)

        coEvery { spyRepository.getOrders() } returns updatedOrders
        coEvery { spyRepository.getOrderById(existingOrderId) } returns existingOrder

        viewModel.updateOrders()

        val expectedViewState = OrdersViewState.UpdateOrders(listOf())
        val actualViewStates = viewModel.viewState.take(1).toList()
        actualViewStates.firstOrNull() shouldBe expectedViewState
        viewModel.numOrdersTrashed shouldBe 0
        viewModel.totalWaste shouldBe "0"
        viewModel.numOrdersDelivered shouldBe 0
        viewModel.totalSales shouldBe "0"
        NumberFormat.getNumberInstance(Locale.US).format(viewModel.totalSales.toInt() - viewModel.totalWaste.toInt())

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
