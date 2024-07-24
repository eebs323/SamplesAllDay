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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.appballstudio.samplesallday.domain.foody.model.FoodyOrderDto
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

const val NAV_ROUTE_FOODY = "ROUTE_FOODY"

@Composable
fun FoodyScreen(
    viewModel: FoodyViewModel = koinViewModel<FoodyViewModelImpl>(),
    lifecycle: Lifecycle
) {
    LaunchedEffect(key1 = lifecycle) { // coroutine tied to the lifecycle of the composable
        viewModel.loadOrders(lifecycle)
    }
    FoodyContent(lifecycle = lifecycle)
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun FoodyContent(
    viewModel: FoodyViewModel = koinViewModel<FoodyViewModelImpl>(),
    lifecycle: Lifecycle
) {
    val coroutineScope = rememberCoroutineScope()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = false,
        onRefresh = {
            coroutineScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.onRefresh(lifecycle)
                }
            }
        }
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pullRefresh(pullRefreshState),  // add scroll behavior for pull to refresh
            contentAlignment = Alignment.Center
        ) {
            ObserveViewState() // compose content based on view state
            PullRefreshIndicator(
                refreshing = false, // only show indicator when refresh action occurs
                state = pullRefreshState, // Pull to refresh action
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
private fun ObserveViewState(
    viewModel: FoodyViewModel = koinViewModel<FoodyViewModelImpl>(),
) {
    val orderViewState by viewModel.orderViewState.collectAsState()
    when (orderViewState) {
        is OrderViewState.Loading -> HandleViewStateLoading()
        is OrderViewState.Error -> HandleViewStateError(orderViewState as OrderViewState.Error)
        is OrderViewState.KitchenClosed -> HandleViewStateKitchenClosed(orderViewState as OrderViewState.KitchenClosed)
        is OrderViewState.GetOrdersSuccess -> HandleViewStateGetOrdersSuccess(orderViewState as OrderViewState.GetOrdersSuccess)
    }
}

@Composable
private fun HandleViewStateLoading() {
    CircularProgressIndicator()
}

@Composable
private fun HandleViewStateGetOrdersSuccess(
    orderViewState: OrderViewState.GetOrdersSuccess
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
fun HandleViewStateKitchenClosed(kitchenClosed: OrderViewState.KitchenClosed) {
    Text(text = stringResource(id = kitchenClosed.messageResId))
}

@Composable
private fun HandleViewStateError(orderViewState: OrderViewState.Error) {
    Text(text = orderViewState.message)
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
fun OrderCard(
    viewModel: FoodyViewModel = koinViewModel<FoodyViewModelImpl>(),
    order: FoodyOrderDto
) {
    val orderCardBackgroundColor = viewModel.getOrderCardBackgroundColor(order)
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