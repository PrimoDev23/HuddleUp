package dev.primodev.huddleup.domain.usecase.event

import dev.primodev.huddleup.domain.repository.EventRepository
import kotlin.uuid.Uuid

class DeleteEventByIdUseCase(
    private val repository: EventRepository,
) {

    fun execute(id: Uuid) = repository.deleteEventById(id)

}