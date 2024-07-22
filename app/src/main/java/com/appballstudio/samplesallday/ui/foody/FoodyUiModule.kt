package com.appballstudio.samplesallday.ui.foody

import com.appballstudio.samplesallday.data.foody.FoodyRepositoryImpl
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val foodyUiModule = module {
    viewModel {
        FoodyViewModelImpl(foodyRepository = get<FoodyRepositoryImpl>())
    }
}