package com.appballstudio.samplesallday.ui.foody

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.appballstudio.samplesallday.domain.FoodyOrder
import org.koin.androidx.compose.koinViewModel

const val ROUTE_FOODY = "ROUTE_FOODY"

@Composable
fun FoodyScreen(lifecycle: Lifecycle) {
    val foodyViewModel: FoodyViewModel = koinViewModel<FoodyViewModelImpl>()
    val orderViewState by foodyViewModel.orderViewState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (orderViewState) {
                is OrderViewState.Loading -> CircularProgressIndicator()
                is OrderViewState.Success -> OnGetOrdersSuccess(
                    orderViewState = orderViewState as OrderViewState.Success
                )

                is OrderViewState.Error -> Text(text = (orderViewState as OrderViewState.Error).message)
            }
        }
    }

    // Load orders only when the screen is visible
    LoadOrders(
        lifecycle = lifecycle,
        foodyViewModel = foodyViewModel
    )
}

@Composable
private fun LoadOrders(
    lifecycle: Lifecycle,
    foodyViewModel: FoodyViewModel
) {
    LaunchedEffect(key1 = lifecycle) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            foodyViewModel.loadOrders()
        }
    }
}

@Composable
private fun OnGetOrdersSuccess(
    orderViewState: OrderViewState.Success
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text( // ORDERS text
            text = "ORDERS",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )
        FoodyOrderList( // Show orders
            orders = orderViewState.orders
        )
    }
}

@Composable
fun FoodyOrderList(orders: List<FoodyOrder>) {
    LazyColumn {
        items(orders) { order ->
            OrderCard(
                order = order
            )
        }
    }
}

@Composable
fun OrderCard(order: FoodyOrder) {
    val foodyViewModel: FoodyViewModel = koinViewModel<FoodyViewModelImpl>()
    val orderCardBackgroundColor = foodyViewModel.getOrderCardBackgroundColor(order)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = orderCardBackgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text( // Item name
                text = order.item,
                modifier = Modifier.weight(1f)
            )
            Text(text = "Shelf: ${order.shelf}") // Shelf name
        }
    }
}