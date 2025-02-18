package dev.primodev.huddleup.appresult

interface ErrorReason {
    data object Unknown : ErrorReason
    data class Exception(val throwable: Throwable) : ErrorReason
}