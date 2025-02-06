package dev.primodev.huddleup.feature.home

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.primodev23.calendar.Calendar
import com.github.primodev23.calendar.CalendarState
import com.github.primodev23.calendar.rememberCalendarState
import dev.primodev.huddleup.R
import dev.primodev.huddleup.domain.entity.event.Event
import dev.primodev.huddleup.domain.entity.event.EventDuration
import dev.primodev.huddleup.extensions.nowAsDateTime
import dev.primodev.huddleup.feature.home.components.CalendarEventBubbleRow
import dev.primodev.huddleup.feature.home.components.EventCard
import dev.primodev.huddleup.feature.home.uistate.HomeUiState
import dev.primodev.huddleup.theme.HuddleUpTheme
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.atDate
import kotlinx.datetime.atTime
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import org.koin.androidx.compose.koinViewModel
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

@Composable
internal fun HomeScreen(viewModel: HomeViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreenContent(
        modifier = Modifier.fillMaxSize(),
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenContent(
    uiState: HomeUiState,
    onEvent: (HomeUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.home_title))
                }
            )
        },
        floatingActionButton = {
            when (uiState) {
                HomeUiState.InitLoading -> Unit

                is HomeUiState.Data -> FloatingActionButton(
                    onClick = {
                        onEvent(HomeUiEvent.AddEventClick)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = stringResource(R.string.home_add_event)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            val baseModifier = Modifier.fillMaxSize()

            when (uiState) {
                HomeUiState.InitLoading -> HomeScreenInitLoading(modifier = baseModifier)

                is HomeUiState.Data -> HomeScreenData(
                    modifier = baseModifier,
                    uiState = uiState,
                    onEvent = onEvent
                )
            }
        }
    }
}

@Composable
private fun HomeScreenInitLoading(modifier: Modifier = Modifier) {
    // TODO: Maybe animate this with some text
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun HomeScreenData(
    uiState: HomeUiState.Data,
    onEvent: (HomeUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val scope = rememberCoroutineScope()
        val calendarState = rememberCalendarState()
        val navigatorText = remember(calendarState.targetMonth) {
            val month = calendarState.targetMonth.startDate.month.getDisplayName(
                TextStyle.FULL,
                Locale.getDefault()
            )

            "$month ${calendarState.targetMonth.startDate.year}"
        }

        HomeScreenCalendarNavigator(
            modifier = Modifier.fillMaxWidth(),
            text = navigatorText,
            onBackClick = {
                scope.launch {
                    calendarState.animateScrollToPreviousMonth()
                }
            },
            onForwardClick = {
                scope.launch {
                    calendarState.animateScrollToNextMonth()
                }
            }
        )

        HomeScreenCalendar(
            modifier = Modifier.width(448.dp),
            selectedDate = uiState.selectedDate,
            onDayClick = { date ->
                onEvent(HomeUiEvent.DayClick(date))
            },
            events = uiState.events,
            calendarState = calendarState,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Crossfade(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            targetState = uiState.selectedDate
        ) { selectedDate ->
            val eventsForSelectedDate = remember(uiState.events, selectedDate) {
                uiState.events[selectedDate].orEmpty()
            }

            HomeScreenEventList(
                modifier = Modifier.fillMaxSize(),
                events = eventsForSelectedDate
            )
        }
    }
}

@Composable
private fun HomeScreenCalendarNavigator(
    text: String,
    onBackClick: () -> Unit,
    onForwardClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Crossfade(
        modifier = modifier,
        targetState = text
    ) { fadedText ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(
                space = 8.dp,
                alignment = Alignment.CenterHorizontally
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = stringResource(R.string.home_calendar_back)
                )
            }

            Text(text = fadedText)

            IconButton(
                onClick = onForwardClick
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                    contentDescription = stringResource(R.string.home_calendar_forward)
                )
            }
        }
    }
}

@Composable
private fun HomeScreenCalendar(
    selectedDate: LocalDate,
    onDayClick: (LocalDate) -> Unit,
    events: Map<LocalDate, List<Event>>,
    calendarState: CalendarState,
    modifier: Modifier = Modifier,
) {
    Calendar(
        modifier = modifier,
        state = calendarState,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        headerContent = {
            HomeScreenCalendarHeader(modifier = Modifier.fillMaxWidth())
        },
        dayContent = { day ->
            val eventsForCurrentDay = remember(events, day) {
                events[day.date].orEmpty()
            }

            ElevatedCard(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f),
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = if (day.isInSelectedMonth) {
                        1.dp
                    } else {
                        0.dp
                    }
                ),
                onClick = {
                    onDayClick(day.date)
                }
            ) {
                val backgroundColor by animateColorAsState(
                    targetValue = if (selectedDate == day.date) {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.38f)
                    } else {
                        Color.Unspecified
                    },
                    label = "DayCardBackground"
                )

                Column(
                    modifier = Modifier
                        .drawBehind {
                            drawRect(color = backgroundColor)
                        }
                        .padding(horizontal = 8.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = day.date.dayOfMonth.toString(),
                        color = if (day.isInSelectedMonth) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        }
                    )

                    if (eventsForCurrentDay.isNotEmpty()) {
                        CalendarEventBubbleRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(
                                alignment = Alignment.CenterHorizontally,
                                space = 4.dp
                            )
                        ) {
                            eventsForCurrentDay.forEach { _ ->
                                Canvas(modifier = Modifier.size(4.dp)) {
                                    drawCircle(color = Color.Black)
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun HomeScreenCalendarHeader(modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        DayOfWeek.entries.forEach { dayOfWeek ->
            val displayName = remember(dayOfWeek) {
                dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
            }

            Text(
                modifier = Modifier.weight(1f),
                text = displayName,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun HomeScreenEventList(
    events: List<Event>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            vertical = 8.dp,
            horizontal = 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = events,
            key = { event ->
                event.id
            }
        ) { event ->
            EventCard(
                modifier = Modifier.fillMaxWidth(),
                event = event
            )
        }
    }
}

@Preview
@Composable
private fun HomeScreenContentPreview(
    @PreviewParameter(HomeUiStateProvider::class) uiState: HomeUiState,
) {
    HuddleUpTheme {
        HomeScreenContent(
            modifier = Modifier.fillMaxSize(),
            uiState = uiState,
            onEvent = {}
        )
    }
}

private class HomeUiStateProvider : PreviewParameterProvider<HomeUiState> {
    private val now = Clock.System.nowAsDateTime()

    override val values: Sequence<HomeUiState> = sequenceOf(
        HomeUiState.InitLoading,
        HomeUiState.Data(
            events = emptyMap(),
            selectedDate = now.date
        ),
        HomeUiState.Data(
            events = mapOf(
                now.date to listOf(
                    Event(
                        duration = EventDuration.AllDay(
                            date = now.date
                        ),
                        title = "All day event"
                    )
                ),
            ),
            selectedDate = now.date
        ),
        HomeUiState.Data(
            events = mapOf(
                now.date to listOf(
                    Event(
                        duration = EventDuration.AllDay(
                            date = Clock.System.nowAsDateTime().date
                        ),
                        title = "Event 1"
                    ),
                    Event(
                        duration = EventDuration.AllDay(
                            date = now.date
                        ),
                        title = "Event 2"
                    ),
                    Event(
                        duration = EventDuration.Specific(
                            start = now.date.atTime(LocalTime(13, 13)),
                            end = now.date.atTime(LocalTime(16, 16))
                        ),
                        title = "Event 3"
                    ),
                ),
                now.date.minus(1, DateTimeUnit.DAY) to listOf(
                    Event(
                        duration = EventDuration.Specific(
                            start = LocalTime(13, 13).atDate(now.date.minus(1, DateTimeUnit.DAY)),
                            end = LocalTime(23, 59, 59).atDate(now.date.plus(1, DateTimeUnit.DAY))
                        ),
                        title = "Event 2"
                    ),
                ),
                now.date.plus(1, DateTimeUnit.DAY) to listOf(
                    Event(
                        duration = EventDuration.Specific(
                            start = LocalTime(0, 0).atDate(now.date.plus(1, DateTimeUnit.DAY)),
                            end = LocalTime(16, 16).atDate(now.date.plus(1, DateTimeUnit.DAY))
                        ),
                        title = "Event 2"
                    ),
                ),
            ),
            selectedDate = now.date
        ),
    )
}