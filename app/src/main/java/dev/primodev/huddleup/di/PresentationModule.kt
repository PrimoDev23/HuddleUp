package dev.primodev.huddleup.di

import dev.primodev.huddleup.navigation.AppNavigator
import dev.primodev.huddleup.navigation.AppNavigatorImpl
import dev.primodev.huddleup.navigation.NavEventProvider
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.binds
import org.koin.dsl.module

val navigationModule = module {
    singleOf(::AppNavigatorImpl) binds arrayOf(AppNavigator::class, NavEventProvider::class)
}