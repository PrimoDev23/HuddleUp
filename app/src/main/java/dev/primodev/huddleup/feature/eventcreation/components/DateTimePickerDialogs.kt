package dev.primodev.huddleup.feature.eventcreation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.primodev.huddleup.R
import dev.primodev.huddleup.extensions.nowAsDateTime
import dev.primodev.huddleup.theme.HuddleUpTheme
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    selectedDate: LocalDate,
    onDismissRequest: () -> Unit,
    onConfirmClick: (LocalDate) -> Unit,
) {
    val datePickerState = rememberDatePickerState()

    LaunchedEffect(true) {
        datePickerState.selectedDateMillis = selectedDate
            .atTime(
                hour = 0,
                minute = 0
            )
            .toInstant(TimeZone.UTC)
            .toEpochMilliseconds()
    }

    androidx.compose.material3.DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    val selectedDateMillis = datePickerState.selectedDateMillis ?: 0
                    val instant = Instant.fromEpochMilliseconds(selectedDateMillis)
                    val date = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date

                    onConfirmClick(date)
                    onDismissRequest()
                }
            ) {
                Text(text = stringResource(R.string.datetime_picker_dialog_confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(text = stringResource(R.string.datetime_picker_dialog_cancel))
            }
        }
    ) {
        DatePicker(
            state = datePickerState
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    selectedTime: LocalTime,
    onDismissRequest: () -> Unit,
    onConfirmClick: (LocalTime) -> Unit,
) {
    val timePickerState = rememberTimePickerState(
        initialHour = selectedTime.hour,
        initialMinute = selectedTime.minute
    )

    Dialog(
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
        onDismissRequest = onDismissRequest
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(
                    start = 24.dp,
                    top = 24.dp,
                    end = 24.dp,
                    bottom = 16.dp
                ),
            ) {
                Text(
                    text = stringResource(R.string.time_picker_dialog_title),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(20.dp))

                TimePicker(
                    state = timePickerState
                )

                Row(modifier = Modifier.align(Alignment.End)) {
                    TextButton(onClick = onDismissRequest) {
                        Text(text = stringResource(R.string.datetime_picker_dialog_cancel))
                    }

                    TextButton(
                        onClick = {
                            onConfirmClick(
                                LocalTime(
                                    hour = timePickerState.hour,
                                    minute = timePickerState.minute
                                )
                            )
                            onDismissRequest()
                        }
                    ) {
                        Text(text = stringResource(R.string.datetime_picker_dialog_confirm))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun DatePickerDialogPreview() {
    HuddleUpTheme {
        DatePickerDialog(
            selectedDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
            onDismissRequest = {},
            onConfirmClick = {}
        )
    }
}

@Preview
@Composable
private fun TimePickerDialogPreview() {
    HuddleUpTheme {
        TimePickerDialog(
            selectedTime = Clock.System.nowAsDateTime().time,
            onDismissRequest = {},
            onConfirmClick = {}
        )
    }
}