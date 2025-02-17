package dev.primodev.huddleup.appresult

sealed interface AppResult<out T> {
    data object Loading : AppResult<Nothing>
    data class Error(val throwable: Throwable?) : AppResult<Nothing>
    data class Success<out T>(val value: T) : AppResult<T>
}