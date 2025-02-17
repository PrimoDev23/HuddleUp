package dev.primodev.huddleup.data.datasource

import dev.primodev.huddleup.appresult.AppResult
import dev.primodev.huddleup.appresult.executeFlowDatabaseRequest
import dev.primodev.huddleup.appresult.executeSuspendingDatabaseRequest
import dev.primodev.huddleup.data.dao.EventDao
import dev.primodev.huddleup.data.datasource.abstractions.EventDatasource
import dev.primodev.huddleup.data.entity.toDomain
import dev.primodev.huddleup.data.entity.toEntity
import dev.primodev.huddleup.domain.entity.event.Event
import kotlinx.coroutines.flow.Flow

class EventDatasourceImpl(
    private val eventDao: EventDao,
) : EventDatasource {
    override fun getAllEvents(): Flow<AppResult<List<Event>>> {
        return executeFlowDatabaseRequest(
            request = eventDao::getAllEvents,
            transform = { entities ->
                entities.map { entity ->
                    entity.toDomain()
                }
            }
        )
    }

    override fun insertEvent(event: Event): Flow<AppResult<Unit>> {
        return executeSuspendingDatabaseRequest(
            request = {
                eventDao.insertEvent(event.toEntity())
            },
            transform = {
                return@executeSuspendingDatabaseRequest it
            }
        )
    }
}