package dev.primodev.huddleup.data.datasource.abstractions

import dev.primodev.huddleup.appresult.AppResult
import dev.primodev.huddleup.domain.entity.event.Event
import kotlinx.coroutines.flow.Flow

interface EventDatasource {
    fun getAllEvents(): Flow<AppResult<List<Event>>>
    fun insertEvent(event: Event): Flow<AppResult<Unit>>
}