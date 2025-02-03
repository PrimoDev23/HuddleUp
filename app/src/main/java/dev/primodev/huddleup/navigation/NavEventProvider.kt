package dev.primodev.huddleup.navigation

import kotlinx.coroutines.flow.Flow

interface NavEventProvider {
    val events: Flow<NavEvent>
}