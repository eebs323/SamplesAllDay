package com.appballstudio.samplesallday.ui.foody.orders

import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.annotation.VisibleForTesting.Companion.PRIVATE
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appballstudio.samplesallday.R
import com.appballstudio.samplesallday.domain.foody.model.ChangelogEntry
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
import java.text.NumberFormat
import java.util.Locale
import kotlin.coroutines.cancellation.CancellationException

interface FoodyViewModel {
    val viewState: StateFlow<OrdersViewState>
    val eventFlow: SharedFlow<Event>
    val numOrdersTrashed: Int
    val numOrdersDelivered: Int
    val totalSales: String
    val totalWaste: String
    val totalRevenue: String

    suspend fun updateOrders()
    fun onOrderClick(order: FoodyOrderDto)
    fun getOrderById(orderId: String): FoodyOrderDto?
    fun getOrderCardBackgroundColor(order: FoodyOrderDto): Color
    fun dispose()
}

class FoodyViewModelImpl(val foodyRepository: FoodyRepository) : ViewModel(), FoodyViewModel {

    private val _viewState = MutableStateFlow<OrdersViewState>(OrdersViewState.Loading)
    override val viewState = _viewState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<Event>()
    override val eventFlow = _eventFlow.asSharedFlow()

    private val _orders = mutableMapOf<String, FoodyOrderDto>() // Store orders by ID

    private var _numOrdersTrashed = 0
    override val numOrdersTrashed: Int get() = _numOrdersTrashed

    private var _numOrdersDelivered = 0
    override val numOrdersDelivered: Int get() = _numOrdersDelivered

    private var _totalSales = 0.0
    override val totalSales: String get() = NumberFormat.getNumberInstance(Locale.US).format(_totalSales)

    private var _totalWaste = 0.0
    override val totalWaste: String get() = NumberFormat.getNumberInstance(Locale.US).format(_totalWaste)

    override val totalRevenue: String get() = NumberFormat.getNumberInstance(Locale.US).format(_totalSales - _totalWaste)

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
                _viewState.value = OrdersViewState.Error(R.string.update_orders_failed)
            }
        }
    }

    override fun onOrderClick(order: FoodyOrderDto) {
        Log.i(TAG, "Order clicked: ${order.item}")
        viewModelScope.launch {
            _eventFlow.emit(Event.NavigateToOrderDetails(order))
        }
    }

    override fun getOrderById(orderId: String): FoodyOrderDto? {
        return _orders[orderId]
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

            if (existingOrder == null) {
                _orders[newOrder.id] = newOrder // Add or update the order
            } else if (newOrder.timestamp > existingOrder.timestamp) {
                updateOrderAndCreateChangelog(
                    order = existingOrder,
                    newState = newOrder.state,
                    newShelf = Shelf.valueOf(newOrder.shelf)
                )
            }
        }
        _viewState.value = OrdersViewState.UpdateOrders(_orders.values.toList())
    }

    private fun updateOrderAndCreateChangelog(
        order: FoodyOrderDto,
        newState: String? = null,
        newShelf: Shelf? = null
    ) {
        val changelogEntries = mutableListOf<ChangelogEntry>()

        if (newState != null && newState != order.state) {
            changelogEntries.add(
                ChangelogEntry(
                    timestamp = System.currentTimeMillis(),
                    changeType = "State Change",
                    oldValue = order.state,
                    newValue = newState
                )
            )
        }

        if (newShelf != null && newShelf != Shelf.valueOf(order.shelf)) {
            changelogEntries.add(
                ChangelogEntry(
                    timestamp = System.currentTimeMillis(),
                    changeType = "Shelf Change",
                    oldValue = order.shelf,
                    newValue = newShelf.name
                )
            )
        }

        if (changelogEntries.isNotEmpty()) {
            if (newState == "TRASHED") {
                _numOrdersTrashed++
                _totalWaste += order.price
            }

            if (newState == "DELIVERED") {
                _numOrdersDelivered++
                _totalSales += order.price
            }

            val updatedOrder = order.copy(
                state = newState ?: order.state,
                shelf = (newShelf ?: Shelf.valueOf(order.shelf)).name,
                changelog = (order.changelog ?: emptyList()) + changelogEntries
            )

            if (newState in listOf("TRASHED", "DELIVERED", "CANCELLED")) {
                _orders.remove(order.id) // Remove terminal state orders
            } else {
                _orders[order.id] = updatedOrder // Update non-terminal state orders
            }
        }
    }
}

sealed class OrdersViewState {
    data object Loading : OrdersViewState()
    data class Error(val messageResId: Int) : OrdersViewState()
    data class KitchenClosed(val messageResId: Int) : OrdersViewState()
    data class UpdateOrders(val orders: List<FoodyOrderDto>) : OrdersViewState()
}

sealed class Event {
    data class NavigateToOrderDetails(val order: FoodyOrderDto) : Event()
    data object Dispose : Event()
}