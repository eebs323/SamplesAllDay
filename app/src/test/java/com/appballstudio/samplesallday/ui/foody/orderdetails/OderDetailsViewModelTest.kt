package com.appballstudio.samplesallday.ui.foody.orderdetails

import com.appballstudio.samplesallday.R
import com.appballstudio.samplesallday.data.foody.repository.FoodyRepositoryImpl
import com.appballstudio.samplesallday.domain.foody.model.FoodyOrderDto
import com.appballstudio.samplesallday.domain.foody.model.Shelf
import com.appballstudio.samplesallday.domain.foody.model.State
import com.appballstudio.samplesallday.domain.foody.repository.FoodyRepository
import com.appballstudio.samplesallday.utils.initTest
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class OderDetailsViewModelTest : FunSpec({
    initTest()

    lateinit var viewModel: OrderDetailsViewModelImpl
    lateinit var mockRepository: FoodyRepository

    beforeTest {
        mockRepository = mockk<FoodyRepositoryImpl>()
        viewModel = OrderDetailsViewModelImpl(
            foodyRepository = mockRepository,
        )
    }

    test("loadOrderDetails should emit Loaded state with order details when successful") {
        val orderId = "order1"
        val order = FoodyOrderDto(
            id = orderId,
            state = State.COOKING.name,
            price = 10,
            item = "Burger",
            shelf = Shelf.HOT.name,
            timestamp = 1678886400000,
            destination = "Table 1"
        )

        every { mockRepository.getOrderById(orderId) } returns order

        viewModel.loadOrderDetails(orderId)

        viewModel.viewState.value shouldBe OrderDetailsViewState.Loaded(order)
    }

    test("loadOrderDetails should emit Error state with error message") {
        val orderId = "order1"
        every { mockRepository.getOrderById(orderId) } returns null

        viewModel.loadOrderDetails(orderId)

        viewModel.viewState.value shouldBe OrderDetailsViewState.Error(R.string.order_not_found)
    }
})