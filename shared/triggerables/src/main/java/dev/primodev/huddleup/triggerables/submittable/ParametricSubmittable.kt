package dev.primodev.huddleup.triggerables.submittable

import dev.primodev.huddleup.appresult.AppResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class ParametricSubmittable<Param, Result>(
    emitInitialValue: Boolean,
    params: Flow<Param>,
    transform: (AppResult<Result>) -> SubmitResult<Result>,
    action: (Param) -> Flow<AppResult<Result>>,
) {

    private val trigger = MutableSharedFlow<Unit>()

    @OptIn(ExperimentalCoroutinesApi::class)
    val flow = params
        .flatMapLatest { innerParams ->
            trigger.flatMapLatest {
                action(innerParams).map(transform)
            }
        }
        .onStart {
            if (emitInitialValue) {
                emit(SubmitResult.Idle)
            }
        }

    suspend fun submit() {
        trigger.emit(Unit)
    }

}

fun <Param, Result> parametricSubmittable(
    params: Flow<Param>,
    emitInitialValue: Boolean = true,
    transform: (AppResult<Result>) -> SubmitResult<Result> = { result ->
        result.toSubmitResult()
    },
    action: (Param) -> Flow<AppResult<Result>>,
) = ParametricSubmittable(
    emitInitialValue = emitInitialValue,
    params = params,
    transform = transform,
    action = action
)