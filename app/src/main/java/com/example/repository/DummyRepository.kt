package com.example.repository

import com.example.data.models.*
import com.example.util.pagination.PaginationResponse
import io.reactivex.Maybe
import io.reactivex.Single

interface DummyRepository {

   // fun loadRecommendations(limit: Int, offset: Int): Maybe<PaginationResponse<Event>>
    fun loadSubscriptions(limit: Int, offset: Int): Maybe<PaginationResponse<Subscription>>
    // fun loadEventSpeakers(event: String, limit: Int, offset: Int): Maybe<PaginationResponse<User>>
    //fun loadFavoriteSpeakers(limit: Int, offset: Int): Maybe<PaginationResponse<User>>
    fun loadUserChats(limit: Int, offset: Int): Maybe<PaginationResponse<UserChat>>
    fun loadUserNotifications(limit: Int, offset: Int): Maybe<PaginationResponse<Notification>>
    fun loadSearchType(): Single<List<SearchTypeEvent>>
    fun loadTags(): Single<List<String>>
   // fun getEvent(): Event
    //fun getUser(event_id: Int): User
}