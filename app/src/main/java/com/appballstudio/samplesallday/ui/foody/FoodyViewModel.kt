package com.appballstudio.samplesallday.ui.foody

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appballstudio.samplesallday.data.foody.FoodyRepository
import com.appballstudio.samplesallday.domain.FoodyOrder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

interface FoodyViewModel {
    val stateFlowOrders: StateFlow<List<FoodyOrder>>
    suspend fun getOrders(): List<FoodyOrder>
    fun loadOrders()
}

class FoodyViewModelImpl(val foodyRepository: FoodyRepository) : ViewModel(), FoodyViewModel {
    private val _stateFlowOrders = MutableStateFlow(listOf<FoodyOrder>())
    override val stateFlowOrders: StateFlow<List<FoodyOrder>> = _stateFlowOrders.asStateFlow()

    override suspend fun getOrders() = foodyRepository.getOrders()

    override fun loadOrders() {
        viewModelScope.launch {
            val orders = getOrders()
            _stateFlowOrders.value = orders
        }
    }

}