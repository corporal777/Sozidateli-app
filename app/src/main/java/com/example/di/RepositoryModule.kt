package com.example.di

import com.example.repository.*
import dagger.Module
import dagger.Provides

@Module
class RepositoryModule {

    @Provides
    fun chatRepository(repository: ChatRepositoryImpl): ChatRepository = repository

    @Provides
    fun authRepository(repository: AuthRepositoryImp): AuthRepository = repository

    @Provides
    fun dummyRepository(repository: DummyRepositoryImpl): DummyRepository = repository

    @Provides
    fun userRepository(repository: UserRepositoryImp): UserRepository = repository

    @Provides
    fun eventRepository(repository: EventRepositoryImp): EventRepository = repository

    @Provides
    fun organizationRepository(repository: OrganizationRepositoryImp): OrganizationRepository = repository

    @Provides
    fun speakerRepository(repository: SpeakerRepositoryImp): SpeakerRepository = repository

    @Provides
    fun dataDataRepository(repository: DataDataRepositoryImp): DataDataRepository = repository
}