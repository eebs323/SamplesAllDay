package com.appballstudio.samplesallday.ui.foody

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appballstudio.samplesallday.domain.foody.repository.FoodyRepository
import com.appballstudio.samplesallday.domain.foody.model.FoodyOrderDto
import com.appballstudio.samplesallday.domain.foody.model.Shelf
import com.appballstudio.samplesallday.ui.foody.theme.LightBlue
import com.appballstudio.samplesallday.ui.foody.theme.LightGray
import com.appballstudio.samplesallday.ui.foody.theme.LightGreen
import com.appballstudio.samplesallday.ui.foody.theme.LightRed
import com.appballstudio.samplesallday.ui.foody.theme.Orange
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

interface FoodyViewModel {
    val orderViewState: StateFlow<OrderViewState>
    fun loadOrders()

    @Composable
    fun getOrderCardBackgroundColor(order: FoodyOrderDto): Color
}

class FoodyViewModelImpl(val foodyRepository: FoodyRepository) : ViewModel(), FoodyViewModel {
    private val _orderViewState = MutableStateFlow<OrderViewState>(OrderViewState.Loading)
    override val orderViewState = _orderViewState.asStateFlow()

    override fun loadOrders() {
        viewModelScope.launch {
            _orderViewState.value = OrderViewState.Loading
            try {
                val orders = foodyRepository.getOrders()!!
                _orderViewState.value = OrderViewState.Success(orders)
            } catch (e: Exception) {
                _orderViewState.value = OrderViewState.Error("Failed to load orders")
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

}

sealed class OrderViewState {
    data object Loading : OrderViewState()
    data class Success(val orders: List<FoodyOrderDto>) : OrderViewState()
    data class Error(val message: String) : OrderViewState()
}