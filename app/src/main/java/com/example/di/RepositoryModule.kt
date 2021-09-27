package com.example.di

import com.example.data.socket.SocketIOManager
import com.example.data.socket.SocketIOManagerImpl
import com.example.repository.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    fun chatRepository(repository: ChatRepositoryImpl): ChatRepository = repository

    @Provides
    fun authRepository(repository: AuthRepositoryImp): AuthRepository = repository

    @Provides
    fun userRepository(repository: UserRepositoryImp): UserRepository = repository

    @Provides
    fun eventRepository(repository: EventRepositoryImp): EventRepository = repository

    @Provides
    fun organizationRepository(repository: OrganizationRepositoryImp): OrganizationRepository = repository

    @Provides
    fun dataDataRepository(repository: DaDataRepositoryImp): DaDataRepository = repository

    @Provides
    fun commonRepository(repository: CommonRepositoryImpl): CommonRepository = repository

    @Provides
    @Singleton
    fun socketRepository(repository: SocketIOManagerImpl): SocketIOManager = repository
}