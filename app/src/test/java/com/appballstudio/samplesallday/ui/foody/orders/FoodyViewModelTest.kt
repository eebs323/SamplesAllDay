package com.appballstudio.samplesallday.ui.foody.orders

import android.util.Log
import com.appballstudio.samplesallday.domain.foody.model.FoodyOrderDto
import com.appballstudio.samplesallday.domain.foody.repository.FoodyRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.first

class FoodyViewModelTest : FunSpec({
    mockkStatic(Log::class)
    every { Log.v(any(), any()) } returns 0
    every { Log.d(any(), any()) } returns 0
    every { Log.i(any(), any()) } returns 0
    every { Log.e(any(), any()) } returns 0

    val mockRepository = mockk<FoodyRepository>()
    val viewModel = FoodyViewModelImpl(mockRepository)

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
        viewModel.viewState.first() shouldBe expectedViewState
    }
})