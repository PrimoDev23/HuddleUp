package dev.primodev.huddleup.triggerables.submittable

import dev.primodev.huddleup.appresult.AppResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class InstantSubmittable<Param, Result>(
    emitInitialValue: Boolean,
    transform: (AppResult<Result>) -> SubmitResult<Result>,
    action: (Param) -> Flow<AppResult<Result>>,
) {

    private val trigger = MutableSharedFlow<Param>()

    @OptIn(ExperimentalCoroutinesApi::class)
    val flow = trigger
        .flatMapLatest { params ->
            action(params).map(transform)
        }
        .onStart {
            if (emitInitialValue) {
                emit(SubmitResult.Idle)
            }
        }

    suspend fun submit(params: Param) {
        trigger.emit(params)
    }
}

suspend fun InstantSubmittable<Unit, *>.submit() = submit(Unit)

fun <Param, Result> instantSubmittable(
    emitInitialValue: Boolean = true,
    transform: (AppResult<Result>) -> SubmitResult<Result> = { result ->
        result.toSubmitResult()
    },
    action: (Param) -> Flow<AppResult<Result>>,
) = InstantSubmittable(
    emitInitialValue = emitInitialValue,
    transform = transform,
    action = action
)