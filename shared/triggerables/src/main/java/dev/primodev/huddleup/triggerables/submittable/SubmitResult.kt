package dev.primodev.huddleup.triggerables.submittable

import dev.primodev.huddleup.appresult.AppResult

sealed interface SubmitResult<out T> {
    data object Idle : SubmitResult<Nothing>
    data object Loading : SubmitResult<Nothing>
    data class Error(val throwable: Throwable?) : SubmitResult<Nothing>
    data class Success<out T>(val value: T) : SubmitResult<T>
}

fun <T> AppResult<T>.toSubmitResult() = when (this) {
    AppResult.Loading -> SubmitResult.Loading
    is AppResult.Error -> SubmitResult.Error(throwable = throwable)
    is AppResult.Success -> SubmitResult.Success(value = value)
}