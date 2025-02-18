package dev.primodev.huddleup.triggerables.submittable

import dev.primodev.huddleup.appresult.AppResult
import dev.primodev.huddleup.appresult.ErrorReason

sealed interface SubmitResult<out T> {
    data object Idle : SubmitResult<Nothing>
    data object Loading : SubmitResult<Nothing>
    data class Error(val reason: ErrorReason) : SubmitResult<Nothing>
    data class Success<out T>(val value: T) : SubmitResult<T>
}

fun <T> AppResult<T>.toSubmitResult() = when (this) {
    AppResult.Loading -> SubmitResult.Loading
    is AppResult.Error -> SubmitResult.Error(reason = reason)
    is AppResult.Success -> SubmitResult.Success(value = value)
}