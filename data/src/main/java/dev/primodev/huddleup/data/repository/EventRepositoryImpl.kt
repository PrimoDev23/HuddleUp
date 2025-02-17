package dev.primodev.huddleup.data.repository

import dev.primodev.huddleup.appresult.AppResult
import dev.primodev.huddleup.data.datasource.abstractions.EventDatasource
import dev.primodev.huddleup.domain.entity.event.Event
import dev.primodev.huddleup.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow

class EventRepositoryImpl(
    private val datasource: EventDatasource,
) : EventRepository {
    override fun getAllEvents(): Flow<AppResult<List<Event>>> {
        return datasource.getAllEvents()
    }

    override fun insertEvent(event: Event): Flow<AppResult<Unit>> {
        return datasource.insertEvent(event)
    }
}