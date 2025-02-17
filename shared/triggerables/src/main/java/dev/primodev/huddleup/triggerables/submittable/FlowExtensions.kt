package dev.primodev.huddleup.triggerables.submittable

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

fun <T> Flow<SubmitResult<T>>.onEachSuccess(action: suspend (T) -> Unit) = onEach { result ->
    when (result) {
        SubmitResult.Idle,
        SubmitResult.Loading,
        is SubmitResult.Error,
            -> Unit

        is SubmitResult.Success -> action(result.value)
    }
}