package com.vicky7230.tasker.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vicky7230.tasker.ViewModelFactory
import com.vicky7230.tasker.di.ViewModelKey
import com.vicky7230.tasker.ui.home.HomeViewModel
import com.vicky7230.tasker.ui.newTask.NewTaskViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    internal abstract fun postHomeViewModel(homeViewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NewTaskViewModel::class)
    internal abstract fun postNewTaskViewModel(newTaskViewModel: NewTaskViewModel): ViewModel

    /*@Binds
    @IntoMap
    @ViewModelKey(AddCityViewModel::class)
    internal abstract fun postAddCityViewModel(addCityViewModel: AddCityViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CityWeatherViewModel::class)
    internal abstract fun postCityWeatherViewModel(cityWeatherViewModel: CityWeatherViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SavedCitiesViewModel::class)
    internal abstract fun postSavedCitiesViewModel(savedCitiesViewModel: SavedCitiesViewModel): ViewModel
*/
}