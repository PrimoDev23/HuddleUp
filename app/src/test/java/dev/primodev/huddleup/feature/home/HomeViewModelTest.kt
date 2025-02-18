package dev.primodev.huddleup.feature.home

import app.cash.turbine.test
import dev.primodev.huddleup.appresult.AppResult
import dev.primodev.huddleup.domain.entity.event.Event
import dev.primodev.huddleup.domain.entity.event.EventDuration
import dev.primodev.huddleup.domain.usecase.event.GetAllEventsUseCase
import dev.primodev.huddleup.extensions.nowAsDateTime
import dev.primodev.huddleup.feature.eventcreation.EventCreationDestination
import dev.primodev.huddleup.feature.home.uistate.HomeUiState
import dev.primodev.huddleup.feature.home.uistate.toEventsPerDay
import dev.primodev.huddleup.navigation.AppNavigator
import dev.primodev.huddleup.testing.KotlinTestBase
import dev.primodev.huddleup.testing.delayedFlowOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.junit.After
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.time.Duration.Companion.days

class HomeViewModelTest : KotlinTestBase() {

    private val getAllEventsUseCase = mockk<GetAllEventsUseCase>()
    private val appNavigator = mockk<AppNavigator>()

    @After
    fun after() {
        unmockkAll()
    }

    // region Init Tests

    @Test
    fun `when initializing events succeeds, then show correct ui states`() {
        // GIVEN
        val now = Clock.System.now()
        val events = createDummyEvents(now)
        val expectedEvents = events
            .flatMap { event ->
                event.toEventsPerDay()
            }
            .groupBy { event ->
                event.start.toLocalDateTime(TimeZone.UTC).date
            }

        mockGetAllEventsUseCaseSuccess(events)

        runTest {
            // WHEN
            createSystemUnderTest().uiState.test {
                // THEN
                assertIs<HomeUiState.InitLoading>(awaitItem())
                awaitItem().let {
                    assertIs<HomeUiState.Data>(it)
                    assertEquals(expectedEvents, it.events)
                    assertEquals(
                        expected = now.toLocalDateTime(TimeZone.currentSystemDefault()).date,
                        actual = it.selectedDate
                    )
                }
            }
        }
    }

    @Test
    fun `when initializing events fails, then show correct ui states`() {
        // GIVEN
        mockGetAllEventsUseCaseError()

        runTest {
            // WHEN
            createSystemUnderTest().uiState.test {
                // THEN
                assertIs<HomeUiState.InitLoading>(awaitItem())
                assertIs<HomeUiState.Error>(awaitItem())
            }
        }
    }

    // endregion Init Tests

    // region Action Tests

    @Test
    fun `when day is clicked, then update selectedDate in ui state`() {
        // GIVEN
        val expectedSelectedDate =
            Clock.System.now().plus(1.days).toLocalDateTime(TimeZone.UTC).date

        val events = createDummyEvents()
        mockGetAllEventsUseCaseSuccess(events)

        val viewModel = createSystemUnderTest()

        runTest {
            viewModel.uiState.test {
                skipItems(2) // InitLoading, Data

                // WHEN
                viewModel.onEvent(HomeUiEvent.DayClick(expectedSelectedDate))

                // THEN
                awaitItem().let {
                    assertIs<HomeUiState.Data>(it)
                    assertEquals(expectedSelectedDate, it.selectedDate)
                }
            }
        }
    }

    @Test
    fun `when add event is clicked, then navigate to EventCreationDestination`() {
        // GIVEN
        mockGetAllEventsUseCaseSuccess()

        coEvery { appNavigator.navigateTo(any()) } returns Unit

        val viewModel = createSystemUnderTest()

        // WHEN
        viewModel.onEvent(HomeUiEvent.AddEventClick)

        // THEN
        coVerify { appNavigator.navigateTo(EventCreationDestination) }
    }

    @Test
    fun `when today is clicked, then update selectedDate to today`() {
        // GIVEN
        val tomorrow = Clock.System.now().plus(1.days).toLocalDateTime(TimeZone.UTC).date
        val expectedSelectedDate = Clock.System.nowAsDateTime().date
        mockGetAllEventsUseCaseSuccess()

        val viewModel = createSystemUnderTest()

        runTest {
            viewModel.uiState.test {
                skipItems(2) // InitLoading, Data

                viewModel.onEvent(HomeUiEvent.DayClick(tomorrow))

                skipItems(1) // Data with updated selectedDate

                // WHEN
                viewModel.onEvent(HomeUiEvent.TodayClick)

                // THEN
                awaitItem().let {
                    assertIs<HomeUiState.Data>(it)
                    assertEquals(expectedSelectedDate, it.selectedDate)
                }
            }
        }
    }

    // endregion Action Tests

    // region Mocking

    private fun mockGetAllEventsUseCaseSuccess(
        events: List<Event> = createDummyEvents(),
    ) {
        every { getAllEventsUseCase.execute() } returns delayedFlowOf(
            AppResult.Loading,
            AppResult.Success(events)
        )
    }

    private fun mockGetAllEventsUseCaseError() {
        every { getAllEventsUseCase.execute() } returns delayedFlowOf(
            AppResult.Loading,
            AppResult.Error()
        )
    }

    // endregion Mocking

    // region Helpers

    private fun createDummyEvents(now: Instant = Clock.System.now()) = listOf(
        Event(
            duration = EventDuration.AllDay,
            start = now,
            end = now,
            title = "Test"
        ),
        Event(
            duration = EventDuration.Specific,
            start = now,
            end = now.plus(1.days),
            title = "Test"
        )
    )

    private fun createSystemUnderTest() = HomeViewModel(
        getAllEventsUseCase = getAllEventsUseCase,
        navigator = appNavigator
    )

    // endregion Helpers

}