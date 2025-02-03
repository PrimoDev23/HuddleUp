package dev.primodev.huddleup.feature.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object HomeDestination

fun NavGraphBuilder.homeGraph() {
    composable<HomeDestination> {
        HomeScreen()
    }
}