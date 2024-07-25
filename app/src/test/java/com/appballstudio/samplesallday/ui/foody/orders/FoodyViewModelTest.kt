package com.appballstudio.samplesallday.ui.foody.orders

import androidx.lifecycle.Lifecycle
import com.appballstudio.samplesallday.domain.foody.repository.FoodyRepository
import com.appballstudio.samplesallday.ui.foody.utils.CoroutineTestRule
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FoodyViewModelTest {

    @MockK
    private lateinit var mockFoodyRepository: FoodyRepository

    @MockK
    private lateinit var mockLifecycle: Lifecycle

    private lateinit var viewModel: FoodyViewModelImpl

    @JvmField
    @Rule
    val coroutineTestRule = CoroutineTestRule()

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)
        viewModel = FoodyViewModelImpl(mockFoodyRepository)
    }

    @Test
    fun `GIVEN GetOrdersSuccess, WHEN loadOrders, THEN show orders`() {
        every  { mockLifecycle.currentState} returns Lifecycle.State.STARTED

        viewModel.loadOrders(mockLifecycle)

        assertTrue(viewModel.viewState.value is OrdersViewState.Error)
    }
}