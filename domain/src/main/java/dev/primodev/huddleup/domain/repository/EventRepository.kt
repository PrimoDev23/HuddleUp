package dev.primodev.huddleup.domain.repository

import dev.primodev.huddleup.appresult.AppResult
import dev.primodev.huddleup.domain.entity.event.Event
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    fun getAllEvents(): Flow<AppResult<List<Event>>>
    fun insertEvent(event: Event): Flow<AppResult<Unit>>
}