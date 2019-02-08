package com.example.repository

import com.example.data.models.Event
import com.example.data.models.Notification
import com.example.data.models.user.User
import com.example.util.pagination.PaginationResponse
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import retrofit2.http.Field

interface EventRepository {
    fun getEventList(limit: Int, offset: Int): Maybe<PaginationResponse<Event>>
}