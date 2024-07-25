package com.appballstudio.samplesallday.ui.foody.orders

import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.DisposableEffect
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
import androidx.navigation.NavHostController
import com.appballstudio.samplesallday.domain.foody.model.FoodyOrderDto
import com.appballstudio.samplesallday.ui.foody.orderdetails.NAV_ROUTE_ORDER_DETAILS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

const val NAV_ROUTE_FOODY = "ROUTE_FOODY"
var ordersJob: Job? = null

@Composable
fun FoodyScreen(
    viewModel: FoodyViewModel = koinViewModel<FoodyViewModelImpl>(),
    lifecycle: Lifecycle,
    navController: NavHostController
) {
    val coroutineScope = rememberCoroutineScope()
    UpdateOrders(coroutineScope = coroutineScope, lifecycle = lifecycle)
    ObserveViewState()
    ObserveViewEvent(navController = navController)
    DisposableEffect(Unit) {
        onDispose {
            viewModel.dispose()
        }
    }
}

@Composable
fun UpdateOrders(
    viewModel: FoodyViewModel = koinViewModel<FoodyViewModelImpl>(),
    coroutineScope: CoroutineScope,
    lifecycle: Lifecycle
) {
    LaunchedEffect(key1 = lifecycle) {
        ordersJob = coroutineScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                while (true) {
                    viewModel.updateOrders()
                    delay(2000) // Check every 2 seconds
                }
            }
        }
    }
}

@Composable
fun ObserveViewState() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val orderViewState by koinViewModel<FoodyViewModelImpl>().viewState.collectAsState()
            when (orderViewState) {
                is OrdersViewState.Loading -> HandleViewStateLoading()
                is OrdersViewState.Error -> HandleViewStateError(orderViewState as OrdersViewState.Error)
                is OrdersViewState.KitchenClosed -> HandleViewStateKitchenClosed(orderViewState as OrdersViewState.KitchenClosed)
                is OrdersViewState.UpdateOrders -> HandleViewStateGetOrdersSuccess(orderViewState as OrdersViewState.UpdateOrders)
            }
        }
    }
}

@Composable
fun ObserveViewEvent(
    viewModel: FoodyViewModel = koinViewModel<FoodyViewModelImpl>(),
    navController: NavHostController,
) {
    LaunchedEffect(key1 = Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is Event.NavigateToOrderDetails -> handleNavigateToOrderDetails(navController, event)
                is Event.Dispose -> handleDispose()
            }
        }
    }
}

@Composable
fun HandleViewStateLoading() {
    CircularProgressIndicator()
}

@Composable
fun HandleViewStateGetOrdersSuccess(
    ordersViewState: OrdersViewState.UpdateOrders
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text( // ORDERS title
            text = "ORDERS",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )
        OrdersList( // Show orders
            orders = ordersViewState.orders
        )
    }
}

@Composable
fun OrdersList(orders: List<FoodyOrderDto>) {
    LazyColumn( // vertically scrolling list
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        items(orders) { order -> // iterate through orders
            OrderCard(order = order)
        }
    }
}

@Composable
fun OrderCard(
    viewModel: FoodyViewModel = koinViewModel<FoodyViewModelImpl>(),
    order: FoodyOrderDto
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { viewModel.onOrderClick(order.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = viewModel.getOrderCardBackgroundColor(order)
        )
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

@Composable
fun HandleViewStateKitchenClosed(kitchenClosed: OrdersViewState.KitchenClosed) {
    Text(text = stringResource(id = kitchenClosed.messageResId))
}

@Composable
private fun HandleViewStateError(errorViewState: OrdersViewState.Error) {
    Text(text = stringResource(id = errorViewState.messageResId))
}

private fun handleNavigateToOrderDetails(navController: NavHostController, event: Event.NavigateToOrderDetails) {
    navController.navigate("$NAV_ROUTE_ORDER_DETAILS/${event.orderId}")
}

private fun handleDispose() {
    ordersJob?.cancel()
    ordersJob = null
}