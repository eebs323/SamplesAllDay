package com.appballstudio.samplesallday.ui.foody

import android.util.Log
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.repeatOnLifecycle
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

interface FoodyViewModel {
    val orderViewState: StateFlow<OrderViewState>
    fun loadOrders(lifecycle: Lifecycle)
    fun onRefresh(lifecycle: Lifecycle)

    @Composable
    fun getOrderCardBackgroundColor(order: FoodyOrderDto): Color
}

class FoodyViewModelImpl(val foodyRepository: FoodyRepository) : ViewModel(), FoodyViewModel {
    private val _orderViewState = MutableStateFlow<OrderViewState>(OrderViewState.Loading)
    override val orderViewState = _orderViewState.asStateFlow()
    private var ordersJob: Job? = null

    private val _orders = mutableMapOf<String, FoodyOrderDto>() // Store orders by ID

    override fun loadOrders(lifecycle: Lifecycle) {
        ordersJob?.cancel()
        ordersJob = viewModelScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                while (isActive) { // Keep checking for updates
                    Log.i(TAG, "Checking for order updates")
                    try {
                        delay(3000)
                        val newOrders = foodyRepository.getOrders()!!
                        if (newOrders.isEmpty()) {
                            _orderViewState.value =
                                OrderViewState.KitchenClosed(R.string.kitchen_closed)
                        } else {
                            updateOrders(newOrders)
                        }
                        Log.i(TAG, "Orders updated")
                    } catch (e: Exception) {
                        Log.e(TAG, "Error updating orders", e)
                        if (e !is CancellationException) {
                            _orderViewState.value = OrderViewState.Error(
                                message = "Failed to update orders"
                            )
                        }
                    }
                    delay(2000) // Check every 2 seconds
                }
            }
        }
    }

    @Composable
    override fun getOrderCardBackgroundColor(order: FoodyOrderDto): Color {
        return when (order.shelf) {
            Shelf.HOT.name -> LightRed
            Shelf.COLD.name -> LightBlue
            Shelf.FROZEN.name -> LightGray
            Shelf.OVERFLOW.name -> Orange
            Shelf.NONE.name -> LightGreen
            else -> MaterialTheme.colorScheme.surface
        }
    }

    private fun updateOrders(newOrders: List<FoodyOrderDto>) {
        for (newOrder in newOrders) {
            val existingOrder = _orders[newOrder.id]
            if (existingOrder == null || newOrder.timestamp > existingOrder.timestamp) {
                _orders[newOrder.id] = newOrder // Add or update the order
            }
        }
        _orderViewState.value = OrderViewState.GetOrdersSuccess(_orders.values.toList())
    }

    override fun onRefresh(lifecycle: Lifecycle) {
        Log.e(TAG, "onRefresh")
        loadOrders(lifecycle)
    }

}

sealed class OrderViewState {
    data object Loading : OrderViewState()
    data class Error(val message: String) : OrderViewState()
    data class KitchenClosed(val messageResId: Int) : OrderViewState()
    data class GetOrdersSuccess(val orders: List<FoodyOrderDto>) : OrderViewState()
}