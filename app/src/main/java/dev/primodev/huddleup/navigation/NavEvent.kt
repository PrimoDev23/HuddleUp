package dev.primodev.huddleup.navigation

sealed interface NavEvent {
    data object NavigateUp : NavEvent
    data class NavigateTo(val destination: Any) : NavEvent
}