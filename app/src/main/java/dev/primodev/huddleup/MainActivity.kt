package dev.primodev.huddleup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import dev.primodev.huddleup.data.di.databaseModule
import dev.primodev.huddleup.data.di.datasourceModule
import dev.primodev.huddleup.data.di.repositoryModule
import dev.primodev.huddleup.di.navigationModule
import dev.primodev.huddleup.di.viewModelModule
import dev.primodev.huddleup.domain.di.useCaseModule
import dev.primodev.huddleup.feature.eventcreation.eventCreationGraph
import dev.primodev.huddleup.feature.home.HomeDestination
import dev.primodev.huddleup.feature.home.homeGraph
import dev.primodev.huddleup.navigation.NavEvent
import dev.primodev.huddleup.navigation.NavEventProvider
import dev.primodev.huddleup.theme.HuddleUpTheme
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KoinApplication(
                application = {
                    androidContext(this@MainActivity)
                    androidLogger()

                    modules(
                        navigationModule,
                        viewModelModule,
                        databaseModule,
                        datasourceModule,
                        repositoryModule,
                        useCaseModule
                    )
                }
            ) {
                HuddleUpTheme {
                    val navController = rememberNavController()
                    val navEventProvider = koinInject<NavEventProvider>()

                    LaunchedEffect(true) {
                        navEventProvider.events.collect { event ->
                            when (event) {
                                NavEvent.NavigateUp -> navController.navigateUp()
                                is NavEvent.NavigateTo -> navController.navigate(event.destination)
                            }
                        }
                    }

                    NavHost(
                        modifier = Modifier.fillMaxSize(),
                        navController = navController,
                        startDestination = HomeDestination
                    ) {
                        homeGraph()
                        eventCreationGraph()
                    }
                }
            }
        }
    }
}