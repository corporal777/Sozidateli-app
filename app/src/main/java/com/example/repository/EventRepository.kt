package com.example.repository

import com.example.data.models.*
import com.example.data.models.user.User
import com.example.util.pagination.PaginationResponse
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface EventRepository {
    fun getEventList(limit: Int, offset: Int, filter: Map<String, Any>? = null): Maybe<PaginationResponse<Event>>
    fun getEventRecommendations(limit: Int, offset: Int): Maybe<PaginationResponse<Event>>
    fun getNewsById(eventId: Int, newsId: Int): Single<News>
    fun getEventRegisterField(eventId: String): Single<RegisterFieldResponse>
    fun eventRegister(eventId: String, fields: Map<String, RequestBody?>, files: List<MultipartBody.Part?>?): Completable
    fun getEventRegister(eventId: String): Single<EventRegisterResponse>
    fun getEventActivity(eventId: Int): Maybe<List<SubEvent>>
    fun getEventInfo(eventId: String): Maybe<EventInfo>
    fun setDefaultEvent(eventId: String): Completable
    fun addEventToCalendar(eventId: String, subEventId: Int): Completable
    fun removeEventFromCalendar(eventId: String, subEventId: Int): Completable
    fun getPartnerListByEvent(eventId: String): Single<List<Partner>>
    fun getPartnerById(eventId: String, partnerId: String): Single<Partner>
    fun getSubevent(eventId: Int, subEventId: Int): Single<SubeventInfo>
    fun getSubeventUsers(eventId: Int, subEventId: Int, limit: Int, offset: Int): Maybe<PaginationResponse<User>>
    fun getCategoriesList(): Single<List<Category>>
    fun getEventSpeakers(eventId: String, limit: Int, offset: Int): Maybe<PaginationResponse<Speaker>>
    fun setEventRating(eventId: Int, value: Int): Completable
    fun getEventByCode(code: String): Single<Event>
    fun getPage(event: String, page: String): Single<Page>
}