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
    fun getEventList(limit: Int, offset: Int,
                     name: String? = null, dateStart: String? = null,
                     dateEnd: String? = null, category: List<String>? = null,
                     organisation: List<String>? = null,
                     qr: String? = null): Maybe<PaginationResponse<Event>>

    fun getEventNewsList(eventId: Int, limit: Int, offset: Int): Maybe<PaginationResponse<News>>
    fun getNewsById(eventId: Int, newsId: Int): Single<News>
    fun getEventDocuments(eventId: Int, limit: Int, offset: Int): Maybe<PaginationResponse<Document>>
    fun getEventRegisterField(eventId: Int): Single<RegisterFieldResponse>
    fun eventRegister(eventId: Int, fields: Map<String, RequestBody?>, files: List<MultipartBody.Part?>?): Completable
    fun getEventRegister(eventId: Int): Single<EventRegisterResponse>
    fun getEventDaySchedule(eventId: Int, body: Map<String, Any>, limit: Int, offset: Int,tags: List<String>): Maybe<PaginationResponse<SubEvent>>
    fun getEventInfo(eventId: Int): Maybe<EventInfo>
    fun getEventRegisterList(limit: Int, offset: Int): Maybe<PaginationResponse<EventRegisterResponse>>
    fun setDefaultEvent(eventId: Int): Completable
    fun addEventToCalendar(eventId: Int, subEventId: Int): Completable
    fun removeEventFromCalendar(eventId: Int, subEventId: Int): Completable
    fun getEventMapInfo(eventId: Int): Single<MapInfo>
    fun getPartnerListByEvent(eventId: Int): Single<List<Partner>>
    fun getPartnerById(partnerId: Int): Single<Partner>
    fun getSubevent(eventId: Int, subEventId: Int): Single<SubeventInfo>
    fun getSubeventUsers(eventId: Int, subEventId: Int, limit: Int, offset: Int): Maybe<PaginationResponse<User>>
    fun getCategoriesList(): Single<List<Category>>
    fun getEventSpeakers(eventId: Int, limit: Int, offset: Int): Maybe<PaginationResponse<Speaker>>
}