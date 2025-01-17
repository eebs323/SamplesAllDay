package com.appballstudio.samplesallday.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.appballstudio.dicebomb.ui.DiceRoller
import com.appballstudio.dicebomb.ui.NAV_ROUTE_DICEY
import com.appballstudio.samplesallday.ui.foody.orderdetails.NAV_ARG_ORDER_ID
import com.appballstudio.samplesallday.ui.foody.orderdetails.NAV_ROUTE_ORDER_DETAILS
import com.appballstudio.samplesallday.ui.foody.orderdetails.OrderDetailsScreen
import com.appballstudio.samplesallday.ui.foody.orders.FoodyScreen
import com.appballstudio.samplesallday.ui.foody.orders.NAV_ROUTE_FOODY
import com.appballstudio.samplesallday.ui.theme.SamplesAllDayTheme
import com.appballstudio.samplesallday.ui.welcome.WelcomeScreen

const val NAV_ROUTE_MAIN = "ROUTE_MAIN"

class MainActivity : AppCompatActivity(), LifecycleOwner {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SamplesAllDayTheme {
                AppNavigation(lifecycle)
            }
        }
    }
}

@Composable
fun AppNavigation(lifecycle: Lifecycle) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = NAV_ROUTE_MAIN) {
        // Welcome
        composable(route = NAV_ROUTE_MAIN) {
            WelcomeScreen(navController = navController)
        }

        // Orders
        composable(route = NAV_ROUTE_FOODY) {
            FoodyScreen(
                lifecycle = lifecycle,
                navController = navController
            )
        }

        // Order Details
        composable(
            route = "$NAV_ROUTE_ORDER_DETAILS/{$NAV_ARG_ORDER_ID}",
            arguments = listOf(navArgument(NAV_ARG_ORDER_ID) { type = NavType.StringType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString(NAV_ARG_ORDER_ID)!!
            OrderDetailsScreen(orderId = orderId)
        }

        // Dice Roller
        composable(NAV_ROUTE_DICEY) {
            DiceRoller()
        }
    }
}