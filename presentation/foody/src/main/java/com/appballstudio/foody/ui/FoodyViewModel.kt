package com.appballstudio.foody.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appballstudio.domain.foody.FoodyOrder
import com.appballstudio.domain.foody.FoodyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

interface FoodViewModel {
    val stateFlowOrders: StateFlow<List<FoodyOrder>>
    fun loadOrders()
}

class FoodyViewModelImpl constructor(val foodyRepository: FoodyRepository) : ViewModel(),
    FoodViewModel {
    private val _stateFlowOrders = MutableStateFlow(listOf<FoodyOrder>())
    override val stateFlowOrders: StateFlow<List<FoodyOrder>> = _stateFlowOrders.asStateFlow()

    override fun loadOrders() {
        viewModelScope.launch {
            val orders = foodyRepository.getOrders()
            _stateFlowOrders.value = orders
        }
    }

}