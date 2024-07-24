package com.appballstudio.samplesallday.ui.foody

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
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
import com.appballstudio.samplesallday.domain.foody.model.FoodyOrderDto
import org.koin.androidx.compose.koinViewModel

const val NAV_ROUTE_FOODY = "ROUTE_FOODY"

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FoodyScreen(lifecycle: Lifecycle) {
    val viewModel: FoodyViewModel = koinViewModel<FoodyViewModelImpl>()
    val orderViewState by viewModel.orderViewState.collectAsState()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = false,
        onRefresh = {
            viewModel.onRefresh()
        }
    )

    FoodyContent(pullRefreshState, orderViewState)

    LaunchedEffect(key1 = lifecycle) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.loadOrders() // load orders on startup
        }
    }
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun FoodyContent(
    pullRefreshState: PullRefreshState,
    orderViewState: OrderViewState,
) {
    Surface( // Screen
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pullRefresh(pullRefreshState), // Pull to refresh behavior
            contentAlignment = Alignment.Center
        ) {
            ObserveViewState(orderViewState) // compose content based on view state
            PullRefreshIndicator(
                refreshing = false, // only show indicator when refresh action occurs
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
private fun ObserveViewState(orderViewState: OrderViewState) {
    when (orderViewState) {
        is OrderViewState.Loading -> CircularProgressIndicator()
        is OrderViewState.Success -> HandleGetOrdersSuccess(orderViewState = orderViewState)
        is OrderViewState.Error -> Text(text = orderViewState.message)
    }
}

@Composable
private fun HandleGetOrdersSuccess(
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
fun FoodyOrderList(orders: List<FoodyOrderDto>) {
    LazyColumn { // vertically scrolling list
        items(orders) { order -> //iterate through orders
            OrderCard(
                order = order
            )
        }
    }
}

@Composable
fun OrderCard(order: FoodyOrderDto) {
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