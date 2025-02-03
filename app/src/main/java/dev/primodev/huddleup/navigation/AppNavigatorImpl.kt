package dev.primodev.huddleup.navigation

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class AppNavigatorImpl : AppNavigator, NavEventProvider {

    private val _events = Channel<NavEvent>()
    override val events: Flow<NavEvent> = _events.receiveAsFlow()

    override suspend fun navigateUp() {
        _events.send(NavEvent.NavigateUp)
    }

    override suspend fun navigateTo(destination: Any) {
        _events.send(NavEvent.NavigateTo(destination = destination))
    }
}