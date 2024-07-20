package com.appballstudio.foody.ui

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val foodyUiModule = module {
    viewModel {
        FoodyViewModelImpl(foodyRepository = get())
    }
}