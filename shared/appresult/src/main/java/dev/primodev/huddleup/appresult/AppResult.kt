package dev.primodev.huddleup.appresult

sealed interface AppResult<out T> {
    data object Loading : AppResult<Nothing>
    data class Error(val reason: ErrorReason = ErrorReason.Unknown) : AppResult<Nothing>
    data class Success<out T>(val value: T) : AppResult<T>
}