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
    fun getOrderCardBackgroundColor(order: FoodyOrderDto): Color
    fun dispose()
}

class FoodyViewModelImpl(
    val foodyRepository: FoodyRepository,
) : ViewModel(), FoodyViewModel {

    private val _viewState = MutableStateFlow<OrdersViewState>(OrdersViewState.Loading)
    override val viewState = _viewState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<Event>()
    override val eventFlow = _eventFlow.asSharedFlow()

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
            val orders = foodyRepository.getOrders()!!
            if (orders.isEmpty()) {
                _viewState.value = OrdersViewState.KitchenClosed(R.string.kitchen_closed)
            } else {
                updateOrders(orders)
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
            _eventFlow.emit(Event.NavigateToOrderDetails(order.id))
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
    internal fun updateOrders(updatedOrders: List<FoodyOrderDto>) {
        for (updatedOrder in updatedOrders) {
            val existingOrder = foodyRepository.getOrderById(updatedOrder.id)

            if (existingOrder == null) {
                foodyRepository.setOrderById( // Add new order
                    orderId = updatedOrder.id,
                    newOrder = updatedOrder
                )
            } else if (updatedOrder.timestamp > existingOrder.timestamp) {
                updateOrderAndCreateChangelog(  // Update existing order
                    existingOrder = existingOrder,
                    newState = updatedOrder.state,
                    newShelf = Shelf.valueOf(updatedOrder.shelf)
                )
            }
        }
        _viewState.value = OrdersViewState.UpdateOrders(foodyRepository.orders.values.toList())
    }

    private fun updateOrderAndCreateChangelog(
        existingOrder: FoodyOrderDto,
        newState: String? = null,
        newShelf: Shelf? = null
    ) {
        val changelogEntries = mutableListOf<ChangelogEntry>()

        if (newState != null && newState != existingOrder.state) {
            changelogEntries.add(
                ChangelogEntry(
                    timestamp = System.currentTimeMillis(),
                    changeType = "State Change",
                    oldValue = existingOrder.state,
                    newValue = newState
                )
            )
        }

        if (newShelf != null && newShelf != Shelf.valueOf(existingOrder.shelf)) {
            changelogEntries.add(
                ChangelogEntry(
                    timestamp = System.currentTimeMillis(),
                    changeType = "Shelf Change",
                    oldValue = existingOrder.shelf,
                    newValue = newShelf.name
                )
            )
        }

        if (changelogEntries.isNotEmpty()) {
            if (newState == "TRASHED") {
                _numOrdersTrashed++
                _totalWaste += existingOrder.price
            }

            if (newState == "DELIVERED") {
                _numOrdersDelivered++
                _totalSales += existingOrder.price
            }

            val updatedOrder = existingOrder.copy(
                state = newState ?: existingOrder.state,
                shelf = (newShelf ?: Shelf.valueOf(existingOrder.shelf)).name,
                changelog = (existingOrder.changelog ?: emptyList()) + changelogEntries
            )

            if (newState in listOf("TRASHED", "DELIVERED", "CANCELLED")) {
                foodyRepository.removeOrderById(existingOrder.id) // Remove terminal state orders
            } else {
                foodyRepository.setOrderById( // Update non-terminal state orders
                    orderId = existingOrder.id,
                    newOrder = updatedOrder
                )
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
    data class NavigateToOrderDetails(val orderId: String) : Event()
    data object Dispose : Event()
}