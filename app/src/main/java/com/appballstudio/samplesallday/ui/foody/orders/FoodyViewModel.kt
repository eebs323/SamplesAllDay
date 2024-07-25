package com.appballstudio.samplesallday.ui.foody.orders

import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.annotation.VisibleForTesting.Companion.PRIVATE
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appballstudio.samplesallday.R
import com.appballstudio.samplesallday.domain.foody.model.FoodyOrderDto
import com.appballstudio.samplesallday.domain.foody.model.Shelf
import com.appballstudio.samplesallday.domain.foody.repository.FoodyRepository
import com.appballstudio.samplesallday.extensions.TAG
import com.appballstudio.samplesallday.ui.foody.theme.LightBlue
import com.appballstudio.samplesallday.ui.foody.theme.LightGray
import com.appballstudio.samplesallday.ui.foody.theme.LightGreen
import com.appballstudio.samplesallday.ui.foody.theme.LightRed
import com.appballstudio.samplesallday.ui.foody.theme.Orange
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

interface FoodyViewModel {
    val viewState: StateFlow<OrdersViewState>
    val eventFlow: SharedFlow<Event>
    suspend fun updateOrders()
    fun onOrderClick(orderId: String)
    fun getOrderCardBackgroundColor(order: FoodyOrderDto): Color
    fun dispose()
}

class FoodyViewModelImpl(val foodyRepository: FoodyRepository) : ViewModel(), FoodyViewModel {

    private val _viewState = MutableStateFlow<OrdersViewState>(OrdersViewState.Loading)
    override val viewState = _viewState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<Event>()
    override val eventFlow = _eventFlow.asSharedFlow()

    private val _orders = mutableMapOf<String, FoodyOrderDto>() // Store orders by ID

    override suspend fun updateOrders() {
        Log.i(TAG, "Checking for order updates")
        try {
            val newOrders = foodyRepository.getOrders()!!
            if (newOrders.isEmpty()) {
                _viewState.value = OrdersViewState.KitchenClosed(R.string.kitchen_closed)
            } else {
                updateOrders(newOrders)
            }
            Log.i(TAG, "Orders updated")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating orders", e)
            if (e !is CancellationException) {
                _viewState.value = OrdersViewState.Error(message = "Failed to update orders")
            }
        }
    }

    override fun onOrderClick(orderId: String) {
        Log.i(TAG, "Order clicked: $orderId")
        viewModelScope.launch {
            _eventFlow.emit(Event.NavigateToOrderDetails(orderId))
        }
    }

    override fun getOrderCardBackgroundColor(order: FoodyOrderDto): Color {
        return when (order.shelf) {
            Shelf.HOT.name -> LightRed
            Shelf.COLD.name -> LightBlue
            Shelf.FROZEN.name -> LightGray
            Shelf.OVERFLOW.name -> Orange
            Shelf.NONE.name -> LightGreen
            else -> LightGreen
        }
    }

    override fun dispose() {
        Log.i(TAG, "Disposing")
        viewModelScope.launch {
            _eventFlow.emit(Event.Dispose)
        }
    }

    @VisibleForTesting(otherwise = PRIVATE)
    internal fun updateOrders(newOrders: List<FoodyOrderDto>) {
        for (newOrder in newOrders) {
            val existingOrder = _orders[newOrder.id]
            if (existingOrder == null || newOrder.timestamp > existingOrder.timestamp) {
                _orders[newOrder.id] = newOrder // Add or update the order
            }
        }
        _viewState.value = OrdersViewState.UpdateOrders(_orders.values.toList())
    }
}

sealed class OrdersViewState {
    data object Loading : OrdersViewState()
    data class Error(val message: String) : OrdersViewState()
    data class KitchenClosed(val messageResId: Int) : OrdersViewState()
    data class UpdateOrders(val orders: List<FoodyOrderDto>) : OrdersViewState()
}

sealed class Event {
    data class NavigateToOrderDetails(val orderId: String) : Event()
    data object Dispose : Event()
}