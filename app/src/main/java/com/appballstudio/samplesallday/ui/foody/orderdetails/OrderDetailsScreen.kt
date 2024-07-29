package com.appballstudio.samplesallday.ui.foody.orderdetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.appballstudio.samplesallday.domain.foody.model.ChangelogEntry
import com.appballstudio.samplesallday.domain.foody.model.FoodyOrderDto
import com.appballstudio.samplesallday.ui.common.HandleViewStateError
import org.koin.androidx.compose.koinViewModel
import java.util.Date

private var currentMainState: OrderDetailsViewState? = null
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
        color = MaterialTheme.colorScheme.background
    ) {
        val viewState by viewModel.viewState.collectAsState()
        if (currentMainState != viewState) {
            currentMainState = viewState
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
}

@Composable
fun HandleViewStateLoaded(viewState: OrderDetailsViewState.Loaded) {
    val order = viewState.order
    OrderDetailsView(order = order)
}

@Composable
fun OrderDetailsView(order: FoodyOrderDto) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(IntrinsicSize.Min),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Order Details",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(32.dp))
            OrderAttributeRow("ID", order.id)
            OrderAttributeRow("Item", order.item)
            OrderAttributeRow("Price", "$" + order.price.toString())
            OrderAttributeRow("State", order.state)
            OrderAttributeRow("Shelf", order.shelf)
            OrderAttributeRow("Destination", order.destination)
            OrderAttributeRow("Timestamp", Date(order.timestamp).toString())

            OrderAttributeRow("Changelog")
            if (order.changelog.isNullOrEmpty()) {
                Text(
                    text = "No changes",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                order.changelog.forEach { entry ->
                    HorizontalDivider()
                    ChangelogEntryView(entry)
                }
            }
        }
    }
}

@Composable
fun OrderAttributeRow(label: String, value: String? = null) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
        if (!value.isNullOrEmpty()) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun ChangelogEntryView(entry: ChangelogEntry) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = "Change at: ${Date(entry.timestamp)}",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "${entry.changeType}: ${entry.oldValue} -> ${entry.newValue}",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
