package com.appballstudio.samplesallday.ui.foody.di

import com.appballstudio.samplesallday.data.foody.repository.FoodyRepositoryImpl
import com.appballstudio.samplesallday.ui.foody.FoodyViewModelImpl
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val foodyUiModule = module {
    viewModel {
        FoodyViewModelImpl(foodyRepository = get<FoodyRepositoryImpl>())
    }
}