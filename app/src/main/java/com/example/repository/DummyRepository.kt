package com.example.repository

import com.example.data.models.*
import com.example.util.pagination.PaginationResponse
import io.reactivex.Maybe
import io.reactivex.Single

interface DummyRepository {

    fun loadRecommendations(limit: Int, offset: Int): Maybe<PaginationResponse<Event>>
    fun loadSubscriptions(limit: Int, offset: Int): Maybe<PaginationResponse<Subscription>>
    fun loadNews(event: String, limit: Int, offset: Int): Maybe<PaginationResponse<News>>
    fun loadDocuments(event: String, limit: Int, offset: Int): Maybe<PaginationResponse<Document>>
    fun loadEventSpeakers(event: String, limit: Int, offset: Int): Maybe<PaginationResponse<User>>
    fun loadFavoriteSpeakers(limit: Int, offset: Int): Maybe<PaginationResponse<User>>
    fun loadUserChats(limit: Int, offset: Int): Maybe<PaginationResponse<UserChat>>
    fun loadSearchType(): Single<List<SearchTypeEvent>>

    fun getEvent(): Event
}