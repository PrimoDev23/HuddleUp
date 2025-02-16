package dev.primodev.huddleup.feature.eventcreation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.primodev.huddleup.extensions.atTime
import dev.primodev.huddleup.feature.eventcreation.uistate.EventCreationInputState
import dev.primodev.huddleup.feature.eventcreation.uistate.EventCreationUiState
import dev.primodev.huddleup.navigation.AppNavigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.hours

class EventCreationViewModel(
    private val navigator: AppNavigator
) : ViewModel() {

    private val now = Clock.System.now()
    private val initialStart = now
    private val initialEnd = now.plus(1.hours)

    private val inputState = MutableStateFlow(
        EventCreationInputState(
            title = "",
            allDayChecked = false,
            start = initialStart,
            end = initialEnd,
            currentDateTimePickerDialog = CurrentDateTimePickerDialog.None
        )
    )

    val uiState = inputState.map { inputState ->
        EventCreationUiState(
            title = inputState.title,
            allDayChecked = inputState.allDayChecked,
            start = inputState.start,
            end = inputState.end,
            currentDateTimePickerDialog = inputState.currentDateTimePickerDialog
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = EventCreationUiState(
            title = "",
            allDayChecked = false,
            start = initialStart,
            end = initialEnd,
            currentDateTimePickerDialog = CurrentDateTimePickerDialog.None
        )
    )

    fun onEvent(event: EventCreationUiEvent) {
        when (event) {
            is EventCreationUiEvent.TitleChange -> onTitleChange(event.title)
            is EventCreationUiEvent.AllDayCheckedChange -> onAllDayCheckedChange(event.checked)
            is EventCreationUiEvent.CurrentDateTimeDialogChange -> onCurrentDateTimeDialogChange(
                event.dialog
            )

            is EventCreationUiEvent.StartChanged -> onStartChanged(event.instant)
            is EventCreationUiEvent.EndChanged -> onEndChanged(event.instant)
            is EventCreationUiEvent.SaveClick -> onSaveClick()
        }
    }

    private fun onTitleChange(title: String) {
        inputState.update {
            it.copy(title = title)
        }
    }

    private fun onAllDayCheckedChange(checked: Boolean) {
        inputState.update {
            it.copy(allDayChecked = checked)
        }
    }

    private fun onCurrentDateTimeDialogChange(dialog: CurrentDateTimePickerDialog) {
        inputState.update {
            it.copy(currentDateTimePickerDialog = dialog)
        }
    }

    private fun onStartChanged(instant: Instant) {
        inputState.update { state ->
            val startDateTime = state.start.toLocalDateTime(TimeZone.UTC)
            val start = instant.atTime(
                hour = startDateTime.hour,
                minute = startDateTime.minute
            )

            // Handle Start > End
            val end = if (start >= state.end) {
                start
            } else {
                state.end
            }

            state.copy(
                start = start,
                end = end
            )
        }
    }

    private fun onEndChanged(instant: Instant) {
        inputState.update { state ->
            val endDateTime = state.end.toLocalDateTime(TimeZone.UTC)
            val end = instant.atTime(
                hour = endDateTime.hour,
                minute = endDateTime.minute
            )

            // Handle Start > End
            val start = if (end <= state.start) {
                end
            } else {
                state.start
            }

            state.copy(
                start = start,
                end = end
            )
        }
    }

    private fun onSaveClick() {
        viewModelScope.launch {
            val data = uiState.value

            // TODO: Implement Saving

            navigator.navigateUp()
        }
    }

}