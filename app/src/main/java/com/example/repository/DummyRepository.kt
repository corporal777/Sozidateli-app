package com.example.repository

import com.example.data.models.Document
import com.example.data.models.Event
import com.example.data.models.News
import com.example.data.models.Subscription
import com.example.util.pagination.PaginationResponse
import io.reactivex.Maybe

interface DummyRepository {

    fun loadRecommendations(limit: Int, offset: Int): Maybe<PaginationResponse<Event>>
    fun loadSubscriptions(limit: Int, offset: Int): Maybe<PaginationResponse<Subscription>>
    fun loadNews(event: String, limit: Int, offset: Int): Maybe<PaginationResponse<News>>
    fun loadDocuments(event: String, limit: Int, offset: Int): Maybe<PaginationResponse<Document>>
}