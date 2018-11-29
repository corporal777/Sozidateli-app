package com.example.di

import com.example.repository.ChatRepository
import com.example.repository.ChatRepositoryImpl
import dagger.Module
import dagger.Provides

@Module
class RepositoryModule {

    @Provides
    fun chatRepository(repository: ChatRepositoryImpl): ChatRepository = repository
}