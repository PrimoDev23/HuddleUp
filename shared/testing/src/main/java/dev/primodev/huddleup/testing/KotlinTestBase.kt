package dev.primodev.huddleup.testing

import org.junit.Rule

abstract class KotlinTestBase {
    @get:Rule
    val testDispatcherRule = TestDispatcherRule()
}