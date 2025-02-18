package dev.primodev.huddleup.appresult

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

fun <Entity, Domain> executeFlowDatabaseRequest(
    request: () -> Flow<Entity>,
    transform: (Entity) -> Domain,
): Flow<AppResult<Domain>> {
    return flow {
        emit(AppResult.Loading)

        emitAll(
            request()
                .map { entity ->
                    val domain = transform(entity)

                    AppResult.Success(value = domain)
                }.catch { throwable ->
                    AppResult.Error(reason = ErrorReason.Exception(throwable))
                }
        )
    }
}

fun <Entity, Domain> executeSuspendingDatabaseRequest(
    request: suspend () -> Entity,
    transform: (Entity) -> Domain,
): Flow<AppResult<Domain>> {
    return flow {
        emit(AppResult.Loading)

        try {
            val entity = request()
            val domain = transform(entity)

            emit(AppResult.Success(value = domain))
        } catch (throwable: Throwable) {
            emit(AppResult.Error(reason = ErrorReason.Exception(throwable)))
        }
    }
}