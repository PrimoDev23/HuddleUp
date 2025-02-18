package dev.primodev.huddleup.feature.home.uistate

import dev.primodev.huddleup.domain.entity.event.Event
import dev.primodev.huddleup.domain.entity.event.EventDuration
import dev.primodev.huddleup.extensions.atTime
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalTime
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.uuid.Uuid

class EventExtensionsTest {

    // region AllDay

    @Test
    fun `when splitting a single day AllDay event, then return list with a single day AllDay event`() {
        // GIVEN
        val id = Uuid.random()
        val now = Clock.System.now()

        val event = Event(
            id = id,
            duration = EventDuration.AllDay,
            start = now,
            end = now,
            title = "Test"
        )
        val expectedEvents = listOf(
            Event(
                id = id,
                duration = EventDuration.AllDay,
                start = now,
                end = now,
                title = "Test"
            )
        )

        // WHEN
        val eventsPerDay = event.toEventsPerDay()

        // THEN
        assertEquals(expectedEvents, eventsPerDay)
    }

    @Test
    fun `when splitting a multi day AllDay event, then return list with multiple AllDay events`() {
        // GIVEN
        val id = Uuid.random()
        val now = Clock.System.now()

        val event = Event(
            id = id,
            duration = EventDuration.AllDay,
            start = now,
            end = now.plus(2.days),
            title = "Test"
        )
        val expectedEvents = listOf(
            Event(
                id = id,
                duration = EventDuration.AllDay,
                start = now,
                end = now,
                title = "Test"
            ),
            Event(
                id = id,
                duration = EventDuration.AllDay,
                start = now.plus(1.days),
                end = now.plus(1.days),
                title = "Test"
            ),
            Event(
                id = id,
                duration = EventDuration.AllDay,
                start = now.plus(2.days),
                end = now.plus(2.days),
                title = "Test"
            )
        )

        // WHEN
        val eventsPerDay = event.toEventsPerDay()

        // THEN
        assertEquals(expectedEvents, eventsPerDay)
    }

    // endregion AllDay

    // region Specific

    @Test
    fun `when splitting a single day Specific event, then return list with a single day Specific event`() {
        // GIVEN
        val id = Uuid.random()
        val now = Clock.System.now().atTime(10, 0)
        val end = now.plus(2.hours)

        val event = Event(
            id = id,
            duration = EventDuration.Specific,
            start = now,
            end = end,
            title = "Test"
        )
        val expectedEvents = listOf(
            Event(
                id = id,
                duration = EventDuration.Specific,
                start = now,
                end = end,
                title = "Test"
            )
        )

        // WHEN
        val eventsPerDay = event.toEventsPerDay()

        // THEN
        assertEquals(expectedEvents, eventsPerDay)
    }

    @Test
    fun `when splitting a multi day Specific event, then return list with multiple events`() {
        // GIVEN
        val id = Uuid.random()
        val now = Clock.System.now().atTime(10, 0)
        val end = now.plus(2.days)

        val event = Event(
            id = id,
            duration = EventDuration.Specific,
            start = now,
            end = end,
            title = "Test"
        )
        val expectedEvents = listOf(
            Event(
                id = id,
                duration = EventDuration.Specific,
                start = now,
                end = now.atTime(
                    LocalTime(
                        hour = 23,
                        minute = 59,
                        second = 59
                    )
                ),
                title = "Test"
            ),
            Event(
                id = id,
                duration = EventDuration.AllDay,
                start = now.plus(1.days),
                end = now.plus(1.days),
                title = "Test"
            ),
            Event(
                id = id,
                duration = EventDuration.Specific,
                start = end.atTime(
                    LocalTime(
                        hour = 0,
                        minute = 0,
                        second = 0
                    )
                ),
                end = end,
                title = "Test"
            )
        )

        // WHEN
        val eventsPerDay = event.toEventsPerDay()

        // THEN
        assertEquals(expectedEvents, eventsPerDay)
    }

    // endregion Specific

}