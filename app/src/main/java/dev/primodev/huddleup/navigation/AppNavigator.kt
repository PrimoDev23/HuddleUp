package dev.primodev.huddleup.navigation

interface AppNavigator {
    suspend fun navigateUp()
    suspend fun navigateTo(destination: Any)
}