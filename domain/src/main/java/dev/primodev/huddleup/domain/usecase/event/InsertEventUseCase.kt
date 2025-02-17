package dev.primodev.huddleup.domain.usecase.event

import dev.primodev.huddleup.domain.entity.event.Event
import dev.primodev.huddleup.domain.repository.EventRepository

class InsertEventUseCase(
    private val repository: EventRepository,
) {

    fun execute(event: Event) = repository.insertEvent(event)

}