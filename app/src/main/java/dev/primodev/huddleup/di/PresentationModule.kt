package dev.primodev.huddleup.di

import dev.primodev.huddleup.feature.eventcreation.EventCreationViewModel
import dev.primodev.huddleup.feature.home.HomeViewModel
import dev.primodev.huddleup.navigation.AppNavigator
import dev.primodev.huddleup.navigation.AppNavigatorImpl
import dev.primodev.huddleup.navigation.NavEventProvider
import kotlinx.datetime.Clock
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.binds
import org.koin.dsl.module

val navigationModule = module {
    singleOf(::AppNavigatorImpl) binds arrayOf(AppNavigator::class, NavEventProvider::class)
}

val viewModelModule = module {
    single<Clock> { Clock.System }

    viewModelOf(::HomeViewModel)
    viewModelOf(::EventCreationViewModel)
}