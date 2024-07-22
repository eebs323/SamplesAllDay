package com.appballstudio.samplesallday.ui.foody

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.appballstudio.samplesallday.domain.FoodyOrder
import org.koin.androidx.compose.getViewModel

const val ROUTE_FOODY = "ROUTE_FOODY"

@Composable
fun FoodyScreen(lifecycle: Lifecycle) {
    val foodyViewModel: FoodyViewModel = getViewModel<FoodyViewModelImpl>()
    var orders by remember { mutableStateOf<List<FoodyOrder>>(emptyList()) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            LaunchedEffect(key1 = lifecycle) {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    orders = foodyViewModel.getOrders()
                }
            }
            FoodyOrderList(orders)
        }
    }
}

@Composable
fun FoodyOrderList(orders: List<FoodyOrder>) {
    LazyColumn {
        items(orders) { order ->
            OrderCard(order)
        }
    }
}

@Composable
fun OrderCard(order: FoodyOrder) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = order.item,
                modifier = Modifier.weight(1f)
            )
            Text(text = "Shelf: ${order.shelf}")
        }
    }
}
