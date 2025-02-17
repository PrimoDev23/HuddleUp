package dev.primodev.huddleup.domain.usecase.event

import dev.primodev.huddleup.domain.repository.EventRepository

class GetAllEventsUseCase(
    private val repository: EventRepository,
) {

    fun execute() = repository.getAllEvents()

}