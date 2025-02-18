package dev.primodev.huddleup.navigation

import app.cash.turbine.test
import dev.primodev.huddleup.testing.KotlinTestBase
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class AppNavigatorTest : KotlinTestBase() {

    private val appNavigator = AppNavigatorImpl()

    @Test
    fun `when navigateTo is called, then events emit NavigateTo Event`() {
        // GIVEN
        val expectedDestination = "Test1234"

        runTest {
            appNavigator.events.test {
                // WHEN
                appNavigator.navigateTo(expectedDestination)

                // THEN
                awaitItem().let {
                    assertIs<NavEvent.NavigateTo>(it)
                    assertEquals(expectedDestination, it.destination)
                }
            }
        }
    }

    @Test
    fun `when navigateUp is called, then events emit NavigateUp Event`() {
        // GIVEN

        runTest {
            appNavigator.events.test {
                // WHEN
                appNavigator.navigateUp()

                // THEN
                assertIs<NavEvent.NavigateUp>(awaitItem())
            }
        }
    }

}