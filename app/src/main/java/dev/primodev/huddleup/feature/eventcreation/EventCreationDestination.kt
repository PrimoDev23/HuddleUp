package dev.primodev.huddleup.feature.eventcreation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object EventCreationDestination

fun NavGraphBuilder.eventCreationGraph() {
    composable<EventCreationDestination> {
        EventCreationScreen()
    }
}