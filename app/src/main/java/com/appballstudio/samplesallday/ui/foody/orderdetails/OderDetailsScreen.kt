package com.appballstudio.samplesallday.ui.foody.orderdetails

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

const val NAV_ROUTE_ORDER_DETAILS = "NAV_ROUTE_ORDER_DETAILS"
const val NAV_ARG_ORDER_ID = "NAV_ARG_ORDER_ID"

@Composable
fun OrderDetailsScreen(orderId: String?) {
    Text(text = "OrderDetailsScreen", style = MaterialTheme.typography.headlineLarge)
}
