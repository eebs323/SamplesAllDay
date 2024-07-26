package com.appballstudio.samplesallday.ui.foody.orderdetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.appballstudio.samplesallday.domain.foody.model.ChangelogEntry
import com.appballstudio.samplesallday.domain.foody.model.FoodyOrderDto
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

const val NAV_ROUTE_ORDER_DETAILS = "NAV_ROUTE_ORDER_DETAILS"
const val NAV_ARG_ORDER_ID = "NAV_ARG_ORDER_ID"

@Composable
fun OrderDetailsScreen(
    order: FoodyOrderDto?,
) {
    if (order != null) {
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
    } else {
        Text(text = "Order not found")
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
