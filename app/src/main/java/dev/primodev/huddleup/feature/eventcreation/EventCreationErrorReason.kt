package dev.primodev.huddleup.feature.eventcreation

import dev.primodev.huddleup.appresult.ErrorReason

interface EventCreationErrorReason : ErrorReason {
    data object TitleBlank : EventCreationErrorReason
}