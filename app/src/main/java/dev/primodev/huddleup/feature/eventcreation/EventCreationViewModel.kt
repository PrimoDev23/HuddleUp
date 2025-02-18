package dev.primodev.huddleup.feature.eventcreation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.primodev.huddleup.appresult.AppResult
import dev.primodev.huddleup.domain.entity.event.Event
import dev.primodev.huddleup.domain.entity.event.EventDuration
import dev.primodev.huddleup.domain.usecase.event.InsertEventUseCase
import dev.primodev.huddleup.extensions.atTime
import dev.primodev.huddleup.feature.eventcreation.uistate.EventCreationInputState
import dev.primodev.huddleup.feature.eventcreation.uistate.EventCreationUiError
import dev.primodev.huddleup.feature.eventcreation.uistate.EventCreationUiState
import dev.primodev.huddleup.navigation.AppNavigator
import dev.primodev.huddleup.triggerables.submittable.SubmitResult
import dev.primodev.huddleup.triggerables.submittable.parametricSubmittable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.hours
import kotlin.uuid.Uuid

class EventCreationViewModel(
    private val insertEventUseCase: InsertEventUseCase,
    private val navigator: AppNavigator,
) : ViewModel() {

    private val uuid = Uuid.random()

    private val now = Clock.System.now()
    private val initialStart = now
    private val initialEnd = now.plus(1.hours)

    private val currentDialog = MutableStateFlow(EventCreationDialog.None)
    private val inputState = MutableStateFlow(
        EventCreationInputState(
            title = "",
            duration = EventDuration.Specific,
            start = initialStart,
            end = initialEnd,
        )
    )

    private val event = inputState.map { state ->
        Event(
            id = uuid,
            duration = state.duration,
            start = state.start,
            end = state.end,
            title = state.title
        )
    }

    private val saveSubmittable = parametricSubmittable(params = event) { event ->
        val errorReason = event.verify()

        if (errorReason == null) {
            insertEventUseCase.execute(event)
        } else {
            flowOf(AppResult.Error(reason = errorReason))
        }
    }

    private val saveFlow = saveSubmittable
        .flow
        .onEach { result ->
            when (result) {
                SubmitResult.Idle -> Unit
                is SubmitResult.Success -> navigator.navigateUp()

                SubmitResult.Loading -> showDialog(EventCreationDialog.IsSaving)
                is SubmitResult.Error -> showDialog(EventCreationDialog.SavingError)
            }
        }

    val uiState = combine(
        currentDialog,
        inputState,
        saveFlow
    ) { currentDialog, inputState, saveResult ->
        val error = when (saveResult) {
            SubmitResult.Idle,
            SubmitResult.Loading,
            is SubmitResult.Success,
                -> null

            is SubmitResult.Error -> {
                when (saveResult.reason) {
                    EventCreationErrorReason.TitleBlank -> EventCreationUiError.TitleBlank
                    else -> null
                }
            }
        }

        EventCreationUiState(
            title = inputState.title,
            duration = inputState.duration,
            start = inputState.start,
            end = inputState.end,
            currentDialog = currentDialog,
            uiError = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = EventCreationUiState(
            title = "",
            duration = EventDuration.Specific,
            start = initialStart,
            end = initialEnd,
            currentDialog = EventCreationDialog.None,
            uiError = null
        )
    )

    fun onEvent(event: EventCreationUiEvent) {
        when (event) {
            is EventCreationUiEvent.TitleChange -> onTitleChange(event.title)
            is EventCreationUiEvent.DurationChanged -> onAllDayCheckedChange(event.duration)
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

    private fun onAllDayCheckedChange(duration: EventDuration) {
        inputState.update {
            it.copy(duration = duration)
        }
    }

    private fun onCurrentDateTimeDialogChange(dialog: EventCreationDialog) {
        viewModelScope.launch {
            showDialog(dialog)
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
            saveSubmittable.submit()
        }
    }

    private suspend fun showDialog(dialog: EventCreationDialog) {
        currentDialog.emit(dialog)
    }

    private fun Event.verify(): EventCreationErrorReason? {
        return when {
            this.title.isBlank() -> EventCreationErrorReason.TitleBlank
            else -> null
        }
    }

}