package com.examle.data.di

import com.examle.data.repository.AuthRepositoryImp
import com.examle.data.repository.ChatRepositoryImpl
import com.examle.data.repository.CommonRepositoryImpl
import com.examle.data.repository.DaDataRepositoryImp
import com.examle.data.repository.EventRepositoryImp
import com.examle.data.repository.OrganizationRepositoryImp
import com.examle.data.repository.SocketIOManagerImpl
import com.examle.data.repository.UserRepositoryImp
import com.examle.domain.repository.AuthRepository
import com.examle.domain.repository.ChatRepository
import com.examle.domain.repository.CommonRepository
import com.examle.domain.repository.DaDataRepository
import com.examle.domain.repository.EventRepository
import com.examle.domain.repository.OrganizationRepository
import com.examle.domain.repository.SocketIOManager
import com.examle.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
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