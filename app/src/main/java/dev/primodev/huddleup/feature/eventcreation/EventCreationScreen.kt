package dev.primodev.huddleup.feature.eventcreation

import android.text.format.DateFormat
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.primodev.huddleup.R
import dev.primodev.huddleup.domain.entity.event.EventDuration
import dev.primodev.huddleup.extensions.atTime
import dev.primodev.huddleup.extensions.nowAsDateTime
import dev.primodev.huddleup.extensions.plus
import dev.primodev.huddleup.feature.eventcreation.components.DatePickerDialog
import dev.primodev.huddleup.feature.eventcreation.components.EventCreationSavingDialog
import dev.primodev.huddleup.feature.eventcreation.components.EventCreationSavingErrorDialog
import dev.primodev.huddleup.feature.eventcreation.components.EventCreationUnsavedChangesDialog
import dev.primodev.huddleup.feature.eventcreation.components.SliderButton
import dev.primodev.huddleup.feature.eventcreation.components.TimePickerDialog
import dev.primodev.huddleup.feature.eventcreation.uistate.EventCreationContentState
import dev.primodev.huddleup.feature.eventcreation.uistate.EventCreationUiError
import dev.primodev.huddleup.feature.eventcreation.uistate.EventCreationUiState
import dev.primodev.huddleup.theme.HuddleUpTheme
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.char
import org.koin.androidx.compose.koinViewModel
import kotlin.time.Duration.Companion.days

@Composable
internal fun EventCreationScreen(
    viewModel: EventCreationViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    EventCreationContent(
        modifier = Modifier.fillMaxSize(),
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventCreationContent(
    uiState: EventCreationUiState,
    onEvent: (EventCreationUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler {
        onEvent(EventCreationUiEvent.Back)
    }

    Scaffold(
        modifier = modifier.imePadding(),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onEvent(EventCreationUiEvent.Back)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = stringResource(R.string.event_creation_back)
                        )
                    }
                },
                title = {
                    Text(text = stringResource(R.string.event_creation_title))
                }
            )
        },
        bottomBar = {
            BottomAppBar(containerColor = MaterialTheme.colorScheme.surface) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onEvent(EventCreationUiEvent.SaveClick)
                    }
                ) {
                    Text(text = stringResource(R.string.event_creation_save))
                }
            }
        }
    ) { innerPadding ->
        val contentState = uiState.contentState

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            EventCreationTitleTextField(
                modifier = Modifier.fillMaxWidth(),
                value = contentState.title,
                onValueChange = {
                    onEvent(EventCreationUiEvent.TitleChange(it))
                },
                isError = contentState.uiError == EventCreationUiError.TitleBlank
            )

            EventCreationDateSelection(
                modifier = Modifier.fillMaxWidth(),
                duration = contentState.duration,
                start = contentState.start,
                end = contentState.end,
                onEvent = onEvent
            )
        }
    }

    EventCreationDialogs(
        currentDialog = uiState.currentDialog,
        contentState = uiState.contentState,
        onEvent = onEvent
    )
}

@Composable
private fun EventCreationDialogs(
    currentDialog: EventCreationDialog,
    contentState: EventCreationContentState,
    onEvent: (EventCreationUiEvent) -> Unit,
) {
    when (currentDialog) {
        EventCreationDialog.None -> Unit
        EventCreationDialog.StartDate -> DatePickerDialog(
            selectedDate = contentState.start.date,
            onDismissRequest = {
                onEvent(EventCreationUiEvent.DialogDismissed)
            },
            onConfirmClick = {
                onEvent(EventCreationUiEvent.StartDateChanged(it))
            }
        )

        EventCreationDialog.StartTime -> TimePickerDialog(
            selectedTime = contentState.start.time,
            onDismissRequest = {
                onEvent(EventCreationUiEvent.DialogDismissed)
            },
            onConfirmClick = {
                onEvent(EventCreationUiEvent.StartTimeChanged(it))
            }
        )

        EventCreationDialog.EndDate -> DatePickerDialog(
            selectedDate = contentState.end.date,
            onDismissRequest = {
                onEvent(EventCreationUiEvent.DialogDismissed)
            },
            onConfirmClick = {
                onEvent(EventCreationUiEvent.EndDateChanged(it))
            }
        )

        EventCreationDialog.EndTime -> TimePickerDialog(
            selectedTime = contentState.end.time,
            onDismissRequest = {
                onEvent(EventCreationUiEvent.DialogDismissed)
            },
            onConfirmClick = {
                onEvent(EventCreationUiEvent.EndTimeChanged(it))
            }
        )

        EventCreationDialog.IsSaving -> {
            EventCreationSavingDialog()
        }

        EventCreationDialog.SavingError -> {
            EventCreationSavingErrorDialog(
                onDismissRequest = {
                    onEvent(EventCreationUiEvent.DialogDismissed)
                },
            )
        }

        EventCreationDialog.UnsavedChanges -> {
            EventCreationUnsavedChangesDialog(
                onDismissRequest = {
                    onEvent(EventCreationUiEvent.DialogDismissed)
                },
                onConfirmClick = {
                    onEvent(EventCreationUiEvent.DiscardClick)
                }
            )
        }
    }
}

@Composable
private fun EventCreationTitleTextField(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(text = stringResource(R.string.event_creation_title_label))
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Create,
                contentDescription = null,
            )
        },
        singleLine = true,
        isError = isError,
        supportingText = {
            if (isError) {
                Text(text = stringResource(R.string.event_creation_title_error))
            }
        }
    )
}

@Composable
private fun EventCreationDateSelection(
    duration: EventDuration,
    start: LocalDateTime,
    end: LocalDateTime,
    onEvent: (EventCreationUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline
        ),
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            SliderButton(
                modifier = Modifier.fillMaxWidth(),
                items = EventDuration.entries,
                selectedItem = duration,
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline
                )
            ) { innerDuration ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            onEvent(EventCreationUiEvent.DurationChanged(innerDuration))
                        },
                    contentAlignment = Alignment.Center
                ) {
                    val textColor by animateColorAsState(
                        targetValue = if (innerDuration == duration) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        label = "SliderButtonTextColor"
                    )
                    val text = stringResource(
                        when (innerDuration) {
                            EventDuration.AllDay -> R.string.event_creation_all_day
                            EventDuration.Specific -> R.string.event_creation_specific
                        }
                    )

                    Text(
                        text = text,
                        color = textColor
                    )
                }
            }

            Crossfade(
                modifier = Modifier.padding(16.dp),
                targetState = duration
            ) { innerDuration ->
                val baseModifier = Modifier
                    .fillMaxWidth()

                when (innerDuration) {
                    EventDuration.AllDay -> {
                        EventCreationAllDaySettings(
                            modifier = baseModifier,
                            start = start,
                            onStartClick = {
                                onEvent(EventCreationUiEvent.StartDateClick)
                            },
                            end = end,
                            onEndClick = {
                                onEvent(EventCreationUiEvent.EndDateClick)
                            }
                        )
                    }

                    EventDuration.Specific -> {
                        EventCreationSpecificSettings(
                            modifier = baseModifier,
                            start = start,
                            onStartDateClick = {
                                onEvent(EventCreationUiEvent.StartDateClick)
                            },
                            onStartTimeClick = {
                                onEvent(EventCreationUiEvent.StartTimeClick)
                            },
                            end = end,
                            onEndDateClick = {
                                onEvent(EventCreationUiEvent.EndDateClick)
                            },
                            onEndTimeClick = {
                                onEvent(EventCreationUiEvent.EndTimeClick)
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EventCreationAllDaySettings(
    start: LocalDateTime,
    onStartClick: () -> Unit,
    end: LocalDateTime,
    onEndClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val formatter = remember {
            LocalDate.Format {
                dayOfMonth()
                char('.')
                monthNumber()
                char('.')
                year()
            }
        }

        val startString = rememberClickableDateTimeString(
            dateTime = start.date,
            formatter = formatter,
            tag = "StartDate",
            onClick = onStartClick
        )

        val endString = rememberClickableDateTimeString(
            dateTime = end.date,
            formatter = formatter,
            tag = "EndDate",
            onClick = onEndClick
        )

        Text(text = startString)

        Text(text = endString)
    }
}

@Composable
private fun <T> rememberClickableDateTimeString(
    dateTime: T,
    formatter: DateTimeFormat<T>,
    tag: String,
    onClick: () -> Unit,
): AnnotatedString {
    return remember(dateTime, formatter, tag, onClick) {
        buildAnnotatedString {
            val annotation = LinkAnnotation.Clickable(
                tag = tag,
                linkInteractionListener = {
                    onClick()
                }
            )
            withLink(link = annotation) {
                append(formatter.format(dateTime))
            }
        }
    }
}

@Composable
private fun EventCreationSpecificSettings(
    start: LocalDateTime,
    onStartDateClick: () -> Unit,
    onStartTimeClick: () -> Unit,
    end: LocalDateTime,
    onEndDateClick: () -> Unit,
    onEndTimeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val dateFormatter = remember {
            LocalDate.Format {
                dayOfMonth()
                char('.')
                monthNumber()
                char('.')
                year()
            }
        }

        val context = LocalContext.current
        val is24HourFormat = DateFormat.is24HourFormat(context)
        val amMarker = stringResource(R.string.local_time_am_marker)
        val pmMarker = stringResource(R.string.local_time_pm_marker)
        val timeFormatter = remember {
            LocalTime.Format {
                if (is24HourFormat) {
                    hour()
                } else {
                    amPmHour()
                }
                char(':')
                minute()
                char(' ')
                amPmMarker(
                    am = amMarker,
                    pm = pmMarker
                )
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            val startDateString = rememberClickableDateTimeString(
                dateTime = start.date,
                formatter = dateFormatter,
                tag = "StartDate",
                onClick = onStartDateClick
            )

            Text(
                modifier = Modifier.weight(1f),
                text = startDateString
            )

            val startTimeString = rememberClickableDateTimeString(
                dateTime = start.time,
                formatter = timeFormatter,
                tag = "StartTime",
                onClick = onStartTimeClick
            )

            Text(text = startTimeString)
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            val endDateString = rememberClickableDateTimeString(
                dateTime = end.date,
                formatter = dateFormatter,
                tag = "EndDate",
                onClick = onEndDateClick
            )

            Text(
                modifier = Modifier.weight(1f),
                text = endDateString
            )

            val endTimeString = rememberClickableDateTimeString(
                dateTime = end.time,
                formatter = timeFormatter,
                tag = "EndTime",
                onClick = onEndTimeClick
            )

            Text(text = endTimeString)
        }
    }
}

@Preview
@Composable
private fun EventCreationContentPreview(
    @PreviewParameter(EventCreationUiStateProvider::class) uiState: EventCreationUiState,
) {
    HuddleUpTheme {
        EventCreationContent(
            modifier = Modifier.fillMaxSize(),
            uiState = uiState,
            onEvent = {}
        )
    }
}

private class EventCreationUiStateProvider : PreviewParameterProvider<EventCreationUiState> {
    private val now = Clock.System.nowAsDateTime()

    override val values: Sequence<EventCreationUiState> = sequenceOf(
        EventCreationUiState(
            currentDialog = EventCreationDialog.None,
            contentState = EventCreationContentState(
                title = "",
                duration = EventDuration.Specific,
                start = now,
                end = now,
                uiError = null
            )
        ),
        EventCreationUiState(
            currentDialog = EventCreationDialog.None,
            contentState = EventCreationContentState(
                title = "",
                duration = EventDuration.Specific,
                start = now,
                end = now,
                uiError = EventCreationUiError.TitleBlank
            ),
        ),
        EventCreationUiState(
            currentDialog = EventCreationDialog.None,
            contentState = EventCreationContentState(
                title = "Title",
                duration = EventDuration.AllDay,
                start = now,
                end = now.plus(1.days).atTime(13, 13),
                uiError = null
            ),
        ),
        EventCreationUiState(
            currentDialog = EventCreationDialog.None,
            contentState = EventCreationContentState(
                title = "Title",
                duration = EventDuration.Specific,
                start = now,
                end = now.plus(1.days).atTime(13, 13),
                uiError = null
            ),
        ),
        EventCreationUiState(
            currentDialog = EventCreationDialog.StartDate,
            contentState = EventCreationContentState(
                title = "Title",
                duration = EventDuration.Specific,
                start = now,
                end = now.plus(1.days).atTime(13, 13),
                uiError = null
            ),
        ),
        EventCreationUiState(
            currentDialog = EventCreationDialog.StartTime,
            contentState = EventCreationContentState(
                title = "Title",
                duration = EventDuration.Specific,
                start = now,
                end = now.plus(1.days).atTime(13, 13),
                uiError = null
            ),
        ),
        EventCreationUiState(
            currentDialog = EventCreationDialog.EndDate,
            contentState = EventCreationContentState(
                title = "Title",
                duration = EventDuration.Specific,
                start = now,
                end = now.plus(1.days).atTime(13, 13),
                uiError = null
            ),
        ),
        EventCreationUiState(
            currentDialog = EventCreationDialog.EndTime,
            contentState = EventCreationContentState(
                title = "Title",
                duration = EventDuration.Specific,
                start = now,
                end = now.plus(1.days).atTime(13, 13),
                uiError = null
            ),
        ),
        EventCreationUiState(
            currentDialog = EventCreationDialog.IsSaving,
            contentState = EventCreationContentState(
                title = "Title",
                duration = EventDuration.Specific,
                start = now,
                end = now.plus(1.days).atTime(13, 13),
                uiError = null
            ),
        ),
        EventCreationUiState(
            currentDialog = EventCreationDialog.SavingError,
            contentState = EventCreationContentState(
                title = "Title",
                duration = EventDuration.Specific,
                start = now,
                end = now.plus(1.days).atTime(13, 13),
                uiError = null
            ),
        ),
        EventCreationUiState(
            currentDialog = EventCreationDialog.UnsavedChanges,
            contentState = EventCreationContentState(
                title = "Title",
                duration = EventDuration.Specific,
                start = now,
                end = now.plus(1.days).atTime(13, 13),
                uiError = null
            ),
        ),
    )

}