package com.appballstudio.samplesallday.ui.foody.orderdetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.appballstudio.samplesallday.domain.foody.model.ChangelogEntry
import com.appballstudio.samplesallday.domain.foody.model.FoodyOrderDto
import com.appballstudio.samplesallday.ui.foody.theme.HandleViewStateError
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

const val NAV_ROUTE_ORDER_DETAILS = "order_details"
const val NAV_ARG_ORDER_ID = "orderId"

@Composable
fun OrderDetailsScreen(
    viewModel: OrderDetailsViewModel = koinViewModel<OrderDetailsViewModelImpl>(),
    orderId: String?
) {
    viewModel.loadOrderDetails(orderId)
    ObserveViewState()
}

@Composable
fun ObserveViewState(
    viewModel: OrderDetailsViewModel = koinViewModel<OrderDetailsViewModelImpl>(),
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val viewState by viewModel.viewState.collectAsState()
        when (viewState) {
            is OrderDetailsViewState.Loading -> {}
            is OrderDetailsViewState.Error -> HandleViewStateError(
                messageResId = (viewState as OrderDetailsViewState.Error).messageResId
            )

            is OrderDetailsViewState.Loaded -> HandleViewStateLoaded(
                viewState = viewState as OrderDetailsViewState.Loaded
            )
        }
    }
}

@Composable
fun HandleViewStateLoaded(viewState: OrderDetailsViewState.Loaded) {
    val order = viewState.order
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Order Details", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "ID: ${order.id}")
        Text(text = "State: ${order.state}")
        Text(text = "Price: $${order.price}")
        Text(text = "Item: ${order.item}")
        Text(text = "Shelf: ${order.shelf}")
        Text(
            text = "Timestamp: ${
                SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    Locale.getDefault()
                ).format(Date(order.timestamp))
            }"
        )
        Text(text = "Destination: ${order.destination}")
        Text(text = "Changelog")
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))
        if (order.changelog.isEmpty()) {
            Text(text = "No changelog entries available")
        } else {
            LazyColumn {
                items(order.changelog) { entry ->
                    ChangelogEntryItem(entry)
                }
            }
        }
    }
}

@Composable
fun ChangelogEntryItem(entry: ChangelogEntry) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(
            text = "Timestamp: ${
                SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    Locale.getDefault()
                ).format(Date(entry.timestamp))
            }"
        )
        Text(text = "Change Type: ${entry.changeType}")
        Text(text = "Old Value: ${entry.oldValue}")
        Text(text = "New Value: ${entry.newValue}")
        HorizontalDivider()
    }
}
