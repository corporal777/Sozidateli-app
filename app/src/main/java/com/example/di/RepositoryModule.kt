package com.example.di

import com.example.repository.ChatRepository
import com.example.repository.ChatRepositoryImpl
import com.example.repository.DummyRepository
import com.example.repository.DummyRepositoryImpl
import dagger.Module
import dagger.Provides

@Module
class RepositoryModule {

    @Provides
    fun chatRepository(repository: ChatRepositoryImpl): ChatRepository = repository

    @Provides
    fun eventRepository(repository: DummyRepositoryImpl): DummyRepository = repository
}