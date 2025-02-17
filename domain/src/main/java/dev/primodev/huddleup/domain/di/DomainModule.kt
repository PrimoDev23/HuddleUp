package dev.primodev.huddleup.domain.di

import dev.primodev.huddleup.domain.usecase.event.GetAllEventsUseCase
import dev.primodev.huddleup.domain.usecase.event.InsertEventUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val useCaseModule = module {
    factoryOf(::GetAllEventsUseCase)
    factoryOf(::InsertEventUseCase)
}