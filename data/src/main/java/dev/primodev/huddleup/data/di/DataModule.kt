package dev.primodev.huddleup.data.di

import androidx.room.Room
import dev.primodev.huddleup.data.dao.EventDao
import dev.primodev.huddleup.data.database.AppDatabase
import dev.primodev.huddleup.data.datasource.EventDatasourceImpl
import dev.primodev.huddleup.data.datasource.abstractions.EventDatasource
import dev.primodev.huddleup.data.repository.EventRepositoryImpl
import dev.primodev.huddleup.domain.repository.EventRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room
            .databaseBuilder(
                androidContext(),
                AppDatabase::class.java,
                "HuddleUpDB"
            )
            .build()
    }
    factory<EventDao> {
        get<AppDatabase>().eventDao()
    }
}

val repositoryModule = module {
    factoryOf(::EventRepositoryImpl) bind EventRepository::class
}

val datasourceModule = module {
    factoryOf(::EventDatasourceImpl) bind EventDatasource::class
}