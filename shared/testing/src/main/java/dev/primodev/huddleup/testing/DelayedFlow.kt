package dev.primodev.huddleup.testing

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

fun <T> delayedFlowOf(
    vararg elements: T,
    delay: Duration = 50.milliseconds,
) = flowOf(*elements).onEach { delay(delay) }