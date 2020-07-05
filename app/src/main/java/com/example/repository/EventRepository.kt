package com.example.repository

import com.example.data.models.*
import com.example.data.models.user.User
import com.example.util.pagination.PaginationResponse
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import okhttp3.RequestBody

interface EventRepository {
    fun getEventList(limit: Int, offset: Int, filter: Map<String, Any>? = null): Maybe<PaginationResponse<Event?>>
    fun getEventRecommendations(limit: Int, offset: Int): Maybe<PaginationResponse<Event?>>
    fun getEventRegisterField(eventId: String): Single<EventRegisterForm>
    fun eventRegister(eventId: String, body: RequestBody): Single<EventRegisterResponse>
    fun eventRegisterCancel(eventId: String): Completable
    fun getEventRegister(eventId: String): Single<EventRegisterResponse>
    fun getEventRatingForm(eventId: String): Single<List<EventRegisterField>>
    fun setEventRating(eventId: String, body: RequestBody): Completable
    fun getEventRating(eventId: String): Single<EventInfo>
    fun getEventActivity(eventId: String): Maybe<EventActivity>
    fun getEventInfo(eventId: String): Maybe<EventInfo>
    fun setDefaultEvent(eventId: String): Completable
    fun addEventToCalendar(eventId: String, subEventId: String): Completable
    fun removeEventFromCalendar(eventId: String, subEventId: String): Completable
    fun getPartnerListByEvent(eventId: String): Single<List<Partner>>
    fun getPartnerById(eventId: String, partnerId: String): Single<Partner>
    fun getSubevent(eventId: String, subEventId: String): Single<SubeventInfo>
    fun getSubeventUsers(eventId: String, subEventId: String, limit: Int, offset: Int): Maybe<PaginationResponse<User>>
    fun getCategoriesList(): Single<List<EventGroup>>
    fun getEventSpeakers(eventId: String, limit: Int, offset: Int): Maybe<PaginationResponse<Speaker>>
    fun getEventByCode(code: String): Single<QrEvent>
    fun getPage(event: String, page: String): Single<Page>

    fun loadEventRegistrationData(eventId: String): Single<EventRegisterData>
    fun loadEventRatingData(eventId: String): Single<EventRatingData>

    fun getFavoriteEvents(limit: Int, offset: Int): Maybe<PaginationResponse<Event?>>
    fun addToFavorite(eventId: String): Completable
    fun removeFromFavorite(eventId: String): Completable

    fun sendMessageToOrganization(eventId: String, message: String): Completable

    fun subscribeToSubevent(event: String, activity: String): Completable
    fun unsubscribeFromSubEvent(event: String, activity: String): Completable
}