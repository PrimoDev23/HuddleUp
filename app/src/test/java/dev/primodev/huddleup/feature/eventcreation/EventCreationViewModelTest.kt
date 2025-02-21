package dev.primodev.huddleup.feature.eventcreation

import app.cash.turbine.test
import dev.primodev.huddleup.appresult.AppResult
import dev.primodev.huddleup.domain.entity.event.EventDuration
import dev.primodev.huddleup.domain.usecase.event.InsertEventUseCase
import dev.primodev.huddleup.extensions.minus
import dev.primodev.huddleup.extensions.plus
import dev.primodev.huddleup.feature.eventcreation.uistate.EventCreationUiError
import dev.primodev.huddleup.navigation.AppNavigator
import dev.primodev.huddleup.testing.delayedFlowOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.junit.After
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

class EventCreationViewModelTest {

    private val insertEventUseCase = mockk<InsertEventUseCase>()
    private val appNavigator = mockk<AppNavigator>()

    private val now = LocalDateTime(
        year = 2025,
        monthNumber = 2,
        dayOfMonth = 20,
        hour = 10,
        minute = 0
    ).toInstant(TimeZone.UTC)

    private val fakeClock = object : Clock {
        override fun now(): Instant {
            return now
        }
    }

    @After
    fun after() {
        unmockkAll()
    }

    // region Init Tests

    @Test
    fun `when initializing, show correct ui state`() {
        // GIVEN
        val expectedDuration = EventDuration.Specific
        val expectedStart = now.toLocalDateTime(TimeZone.currentSystemDefault())
        val expectedEnd = now.toLocalDateTime(TimeZone.currentSystemDefault()).plus(1.hours)

        runTest {
            // WHEN
            createSystemUnderTest().uiState.test {
                // THEN
                awaitItem().let { uiState ->
                    assertEquals(EventCreationDialog.None, uiState.currentDialog)

                    val contentState = uiState.contentState

                    assertTrue(contentState.title.isEmpty())
                    assertEquals(expectedDuration, contentState.duration)
                    assertEquals(expectedStart, contentState.start)
                    assertEquals(expectedEnd, contentState.end)
                    assertNull(contentState.uiError)
                }
            }
        }
    }

    // endregion Init Tests

    // region Action Tests

    @Test
    fun `when updating title, then ui state changes`() {
        // GIVEN
        val expectedTitle = "Title"
        val viewModel = createSystemUnderTest()

        runTest {
            viewModel.uiState.test {
                skipItems(1) // Initial state

                // WHEN
                viewModel.onEvent(EventCreationUiEvent.TitleChange(expectedTitle))

                // THEN
                assertEquals(expectedTitle, awaitItem().contentState.title)
            }
        }
    }

    @Test
    fun `when updating duration, then ui state changes`() {
        // GIVEN
        val expectedDuration = EventDuration.AllDay
        val viewModel = createSystemUnderTest()

        runTest {
            viewModel.uiState.test {
                skipItems(1) // Initial state

                // WHEN
                viewModel.onEvent(EventCreationUiEvent.DurationChanged(expectedDuration))

                // THEN
                assertEquals(expectedDuration, awaitItem().contentState.duration)
            }
        }
    }

    @Test
    fun `when clicking start date, then show StartDate dialog`() {
        // GIVEN
        val expectedDialog = EventCreationDialog.StartDate
        val viewModel = createSystemUnderTest()

        runTest {
            viewModel.uiState.test {
                skipItems(1) // Initial state

                // WHEN
                viewModel.onEvent(EventCreationUiEvent.StartDateClick)

                // THEN
                assertEquals(expectedDialog, awaitItem().currentDialog)
            }
        }
    }

    @Test
    fun `when clicking start time, then show StartTime dialog`() {
        // GIVEN
        val expectedDialog = EventCreationDialog.StartTime
        val viewModel = createSystemUnderTest()

        runTest {
            viewModel.uiState.test {
                skipItems(1) // Initial state

                // WHEN
                viewModel.onEvent(EventCreationUiEvent.StartTimeClick)

                // THEN
                assertEquals(expectedDialog, awaitItem().currentDialog)
            }
        }
    }

    @Test
    fun `when clicking end date, then show EndDate dialog`() {
        // GIVEN
        val expectedDialog = EventCreationDialog.EndDate
        val viewModel = createSystemUnderTest()

        runTest {
            viewModel.uiState.test {
                skipItems(1) // Initial state

                // WHEN
                viewModel.onEvent(EventCreationUiEvent.EndDateClick)

                // THEN
                assertEquals(expectedDialog, awaitItem().currentDialog)
            }
        }
    }

    @Test
    fun `when clicking end time, then show EndTime dialog`() {
        // GIVEN
        val expectedDialog = EventCreationDialog.EndTime
        val viewModel = createSystemUnderTest()

        runTest {
            viewModel.uiState.test {
                skipItems(1) // Initial state

                // WHEN
                viewModel.onEvent(EventCreationUiEvent.EndTimeClick)

                // THEN
                assertEquals(expectedDialog, awaitItem().currentDialog)
            }
        }
    }

    @Test
    fun `when dialog is dismissed, then set currentDialog to None`() {
        // GIVEN
        val expectedDialog = EventCreationDialog.None
        val viewModel = createSystemUnderTest()

        runTest {
            viewModel.uiState.test {
                skipItems(1) // Initial state

                viewModel.onEvent(EventCreationUiEvent.StartDateClick)

                skipItems(1) // StartDate dialog

                // WHEN
                viewModel.onEvent(EventCreationUiEvent.DialogDismissed)

                // THEN
                assertEquals(expectedDialog, awaitItem().currentDialog)
            }
        }
    }

    // region DateChanged

    @Test
    fun `when start date is changed and is before end, then update start`() {
        // GIVEN
        val expectedStart = now
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .minus(1.days)
        val viewModel = createSystemUnderTest()

        runTest {
            viewModel.uiState.test {
                skipItems(1) // Initial state

                // WHEN
                viewModel.onEvent(EventCreationUiEvent.StartDateChanged(expectedStart.date))

                // THEN
                assertEquals(expectedStart, awaitItem().contentState.start)
            }
        }
    }

    @Test
    fun `when start date is changed and is after end, then update start and end`() {
        // GIVEN
        val expectedStart = now
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .plus(1.days)
        val viewModel = createSystemUnderTest()

        runTest {
            viewModel.uiState.test {
                skipItems(1) // Initial state

                // WHEN
                viewModel.onEvent(EventCreationUiEvent.StartDateChanged(expectedStart.date))

                // THEN
                awaitItem().contentState.let {
                    assertEquals(expectedStart, it.start)
                    assertEquals(expectedStart, it.end)
                }
            }
        }
    }

    @Test
    fun `when end date is changed and is after start, then update end`() {
        // GIVEN
        val expectedEnd = now
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .plus(1.hours)
            .plus(1.days)
        val viewModel = createSystemUnderTest()

        runTest {
            viewModel.uiState.test {
                skipItems(1) // Initial state

                // WHEN
                viewModel.onEvent(EventCreationUiEvent.EndDateChanged(expectedEnd.date))

                // THEN
                assertEquals(expectedEnd, awaitItem().contentState.end)
            }
        }
    }

    @Test
    fun `when end date is changed and is before start, then update start and end`() {
        // GIVEN
        val expectedEnd = now
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .plus(1.hours)
            .minus(1.days)
        val viewModel = createSystemUnderTest()

        runTest {
            viewModel.uiState.test {
                skipItems(1) // Initial state

                // WHEN
                viewModel.onEvent(EventCreationUiEvent.EndDateChanged(expectedEnd.date))

                // THEN
                awaitItem().contentState.let {
                    assertEquals(expectedEnd, it.start)
                    assertEquals(expectedEnd, it.end)
                }
            }
        }
    }

    // endregion Date Changed

    // region Time Changed

    @Test
    fun `when start time is changed and is before end, then update start`() {
        // GIVEN
        val expectedStart = now
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .minus(1.hours)
        val viewModel = createSystemUnderTest()

        runTest {
            viewModel.uiState.test {
                skipItems(1) // Initial state

                // WHEN
                viewModel.onEvent(EventCreationUiEvent.StartTimeChanged(expectedStart.time))

                // THEN
                assertEquals(expectedStart, awaitItem().contentState.start)
            }
        }
    }

    @Test
    fun `when start time is changed and is after end, then update start and end`() {
        // GIVEN
        val expectedStart = now
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .plus(2.hours)
        val viewModel = createSystemUnderTest()

        runTest {
            viewModel.uiState.test {
                skipItems(1) // Initial state

                // WHEN
                viewModel.onEvent(EventCreationUiEvent.StartTimeChanged(expectedStart.time))

                // THEN
                awaitItem().contentState.let {
                    assertEquals(expectedStart, it.start)
                    assertEquals(expectedStart, it.end)
                }
            }
        }
    }

    @Test
    fun `when end time is changed and is after start, then update end`() {
        // GIVEN
        val expectedEnd = now
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .plus(2.hours)
        val viewModel = createSystemUnderTest()

        runTest {
            viewModel.uiState.test {
                skipItems(1) // Initial state

                // WHEN
                viewModel.onEvent(EventCreationUiEvent.EndTimeChanged(expectedEnd.time))

                // THEN
                assertEquals(expectedEnd, awaitItem().contentState.end)
            }
        }
    }

    @Test
    fun `when end time is changed and is before start, then update start and end`() {
        // GIVEN
        val expectedStart = now
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .minus(1.hours)
        val viewModel = createSystemUnderTest()

        runTest {
            viewModel.uiState.test {
                skipItems(1) // Initial state

                // WHEN
                viewModel.onEvent(EventCreationUiEvent.EndTimeChanged(expectedStart.time))

                // THEN
                awaitItem().contentState.let {
                    assertEquals(expectedStart, it.start)
                    assertEquals(expectedStart, it.end)
                }
            }
        }
    }

    // endregion Time Changed

    @Test
    fun `when back is clicked and initial state is set, then navigate up`() {
        // GIVEN
        val viewModel = createSystemUnderTest()

        coEvery { appNavigator.navigateUp() } returns Unit

        // WHEN
        viewModel.onEvent(EventCreationUiEvent.Back)

        // THEN
        coVerify { appNavigator.navigateUp() }
    }

    @Test
    fun `when back is clicked and data was changed, then show unsaved changes dialog`() {
        // GIVEN
        val expectedDialog = EventCreationDialog.UnsavedChanges
        val viewModel = createSystemUnderTest()

        runTest {
            viewModel.uiState.test {
                viewModel.onEvent(EventCreationUiEvent.TitleChange("Title"))

                skipItems(2) // Initial, Title changed

                // WHEN
                viewModel.onEvent(EventCreationUiEvent.Back)

                // THEN
                assertEquals(expectedDialog, awaitItem().currentDialog)
                coVerify(exactly = 0) { appNavigator.navigateUp() }
            }
        }
    }

    @Test
    fun `when discard is clicked, then navigate up`() {
        // GIVEN
        val viewModel = createSystemUnderTest()

        coEvery { appNavigator.navigateUp() } returns Unit

        // WHEN
        viewModel.onEvent(EventCreationUiEvent.DiscardClick)

        // THEN
        coVerify { appNavigator.navigateUp() }
    }

    @Test
    fun `given title is empty, when save is clicked, then show TitleBlank error`() {
        // GIVEN
        val expectedUiError = EventCreationUiError.TitleBlank
        val viewModel = createSystemUnderTest()

        runTest {
            viewModel.uiState.test {
                skipItems(1) // Initial

                delay(50)

                // WHEN
                viewModel.onEvent(EventCreationUiEvent.SaveClick)

                // THEN
                assertEquals(expectedUiError, awaitItem().contentState.uiError)
            }
        }
    }

    @Test
    fun `given title is not empty and insert succeeds, when save is clicked, then call insert use case and navigate up`() {
        // GIVEN
        mockInsertEventSuccess()

        val viewModel = createSystemUnderTest()

        runTest {
            viewModel.uiState.test {
                skipItems(1) // Initial

                viewModel.onEvent(EventCreationUiEvent.TitleChange("Title"))

                skipItems(1) // Title change

                // WHEN
                viewModel.onEvent(EventCreationUiEvent.SaveClick)

                // THEN
                assertEquals(EventCreationDialog.IsSaving, awaitItem().currentDialog)
                verify { insertEventUseCase.execute(any()) }
            }
        }
    }

    @Test
    fun `given title is not empty and insert fails, when save is clicked, then show error`() {
        // GIVEN
        val expectedError = EventCreationDialog.SavingError

        mockInsertEventError()

        val viewModel = createSystemUnderTest()

        runTest {
            viewModel.uiState.test {
                skipItems(1) // Initial

                viewModel.onEvent(EventCreationUiEvent.TitleChange("Title"))

                skipItems(1) // Title change

                // WHEN
                viewModel.onEvent(EventCreationUiEvent.SaveClick)

                // THEN
                assertEquals(EventCreationDialog.IsSaving, awaitItem().currentDialog)
                assertEquals(expectedError, awaitItem().currentDialog)

                verify { insertEventUseCase.execute(any()) }
            }
        }
    }

    // endregion Action Tests

    // region Mocking

    private fun mockInsertEventSuccess() {
        every { insertEventUseCase.execute(any()) } returns delayedFlowOf(
            AppResult.Loading,
            AppResult.Success(Unit)
        )
    }

    private fun mockInsertEventError() {
        every { insertEventUseCase.execute(any()) } returns delayedFlowOf(
            AppResult.Loading,
            AppResult.Error()
        )
    }

    // endregion Mocking

    // region Helpers

    private fun createSystemUnderTest() = EventCreationViewModel(
        insertEventUseCase = insertEventUseCase,
        navigator = appNavigator,
        clock = fakeClock
    )

    // endregion Helpers

}