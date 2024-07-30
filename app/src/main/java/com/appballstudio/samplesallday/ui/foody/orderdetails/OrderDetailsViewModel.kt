package com.appballstudio.samplesallday.ui.foody.orderdetails

import android.util.Log
import androidx.lifecycle.ViewModel
import com.appballstudio.samplesallday.R
import com.appballstudio.samplesallday.domain.foody.model.FoodyOrderDto
import com.appballstudio.samplesallday.domain.foody.repository.FoodyRepository
import com.appballstudio.samplesallday.extensions.TAG
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface OrderDetailsViewModel {
    val viewState: StateFlow<OrderDetailsViewState>

    fun loadOrderDetails(orderId: String?)
}

class OrderDetailsViewModelImpl(val foodyRepository: FoodyRepository) : ViewModel(), OrderDetailsViewModel {

    private val _viewState = MutableStateFlow<OrderDetailsViewState>(OrderDetailsViewState.Loading)
    override val viewState = _viewState.asStateFlow()

    override fun loadOrderDetails(orderId: String?) {
        try {
            val order = foodyRepository.getOrderById(orderId!!)
            _viewState.value = OrderDetailsViewState.Loaded(order!!)
            Log.i(TAG, "order details loaded successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error loading order details", e)
            _viewState.value = OrderDetailsViewState.Error(R.string.order_not_found)
        }
    }
}

sealed class OrderDetailsViewState {
    data object Loading : OrderDetailsViewState()
    data class Error(val messageResId: Int) : OrderDetailsViewState()
    data class Loaded(val order: FoodyOrderDto) : OrderDetailsViewState()
}