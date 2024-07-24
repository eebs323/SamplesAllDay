package com.appballstudio.samplesallday.ui.welcome

import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.appballstudio.dicebomb.ui.NAV_ROUTE_DICEY
import com.appballstudio.samplesallday.ui.foody.orders.NAV_ROUTE_FOODY

interface WelcomeViewModel {
    fun onViewOrdersClick(navController: NavHostController)
    fun onRollDiceClick(navController: NavHostController)
}

class WelcomeViewModelImpl : ViewModel(), WelcomeViewModel {
    override fun onViewOrdersClick(navController: NavHostController) {
        navController.navigate(NAV_ROUTE_FOODY)
    }

    override fun onRollDiceClick(navController: NavHostController) {
        navController.navigate(NAV_ROUTE_DICEY)
    }
}