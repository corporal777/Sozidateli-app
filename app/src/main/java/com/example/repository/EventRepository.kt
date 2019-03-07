package com.example.repository

import com.example.data.models.Document
import com.example.data.models.Event
import com.example.data.models.News
import com.example.data.models.Notification
import com.example.data.models.user.User
import com.example.util.pagination.PaginationResponse
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import retrofit2.http.Field

interface EventRepository {
    fun getEventList(limit: Int, offset: Int): Maybe<PaginationResponse<Event>>
    fun getEventNewsList(eventId:Int,limit: Int, offset: Int): Maybe<PaginationResponse<News>>
    fun getNewsById(eventId:Int,newsId:Int): Single<News>
    fun getEventDocuments(eventId:Int,limit: Int, offset: Int): Maybe<PaginationResponse<Document>>

}