package com.appballstudio.samplesallday.ui.foody.di

import com.appballstudio.samplesallday.data.foody.repository.FoodyRepositoryImpl
import com.appballstudio.samplesallday.ui.foody.orders.FoodyViewModelImpl
import com.appballstudio.samplesallday.ui.welcome.WelcomeViewModelImpl
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val foodyUiModule = module {
    viewModel {
        WelcomeViewModelImpl()
    }
    viewModel {
        FoodyViewModelImpl(foodyRepository = get<FoodyRepositoryImpl>())
    }
}