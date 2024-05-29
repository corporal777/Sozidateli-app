package com.example.repository

import com.example.data.bodies.AddToFavoriteModel
import com.example.data.bodies.EventCalendarBody
import com.example.data.bodies.MessageToEventBody
import com.example.data.models.*
import com.example.data.models.ProfileFieldsData
import com.example.util.pagination.PaginationResponse
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import okhttp3.RequestBody

interface EventRepository {

    fun getEventsList(map: Map<String, Any>): Maybe<PaginationResponse<EventNew?>>
    fun getEventsListNew(map: Map<String, Any>): Maybe<PaginationResponse<EventNew?>>
    fun getSortedEventsList(map: Map<String, Any>): Maybe<PaginationResponse<EventNew?>>
    fun getUserCalendarEvents(): Maybe<List<EventNew>?>

    fun getOrganizationEventsList(map: Map<String, Any>): Maybe<PaginationResponse<EventNew?>>

    fun getEventFormatsList(map: Map<String, Any>): Maybe<List<NewEventFormat>>
    fun getActiveEventFormatsList(): Maybe<List<NewEventFormat>>

    fun getEvent(eventId: String, binds : String?): Maybe<EventNew>
    fun getEventDetails(eventId: String): Maybe<EventNew>
    fun getEventDetailForRegister(eventId: String): Maybe<EventNew>

    fun getEventMember(memberId : String) : Maybe<MemberModel>
    fun createEventSubscription(eventId: Int) : Completable
    fun deleteEventSubscription(eventId : Int) : Completable

    fun mailToEvent(body: MessageToEventBody): Completable
    fun getPageDetails(pageId: String): Single<PageModel>
    fun getSpeakers(map: Map<String, Any>): Maybe<PaginationResponse<MemberModel>>
    fun getSpeakersWithoutPagination(map: Map<String, Any>): Maybe<List<MemberModel>>
    fun getPartnerDetails(partnerId : String): Single<PartnerModel>

    fun addToFavorites(body: AddToFavoriteModel): Single<AddFavoriteModel>

    fun addEventToFavorites(eventId: String) : Single<AddFavoriteModel>
    fun addOrgToFavorites(orgId: String) : Single<AddFavoriteModel>
    fun addUserToFavorites(speakerId: String) : Single<AddFavoriteModel>
    fun deleteFromFavorites(id : String): Completable

    fun checkUserProfile(): Maybe<UserProfileFieldsModel>
    fun getEventFavoritesList(map: Map<String, Any>): Maybe<PaginationResponse<EventNew?>>

    fun getEventForm(map: Map<String, Any>): Single<ApiNewResponse<List<EventFormModel>>>
    fun getEventFormResult(map: Map<String, Any>): Single<ApiNewResponse<List<EventFormResultModel>>>
    //+
    fun getEventFormResultDraft(id : Int, map: Map<String, Any>): Maybe<EventFormResultDraftModel>
    fun saveEventFormResultDraft(body: RequestBody): Single<EventFormResultModel>
    fun loadEventFormResult(id : String): Single<ProfileFieldsData>

    fun eventRegisterNew(body: RequestBody): Single<ApiNewResponse<List<EventFormResultModel>>>
    fun registerToEvent(eventId : Int): Completable
    fun cancelRegisterToEvent(eventId : Int): Completable
    fun addEventToCalendarWithResult(body: EventCalendarBody): Single<EventCalendarModel>
    fun addEventToCalendar(body: EventCalendarBody): Completable
    fun deleteAllCalendarEvents(entityType: String): Completable
    fun deleteCalendarEvent(id: String): Completable
    fun getUserCalendarEvent( entityType: String): Maybe<ApiNewResponse<List<EventCalendarModel>>>
    fun getEventActivities(eventId: Int): Maybe<List<EventActivityModel>>
    fun getEventActivityDetail(activityId: String): Single<EventActivityModel>
    fun getTags(map: Map<String, Any>): Maybe<List<EventTagModel>>

    //+
    fun searchEvents(map: Map<String, Any>): Maybe<PaginationResponse<EventNew?>>

    //+
    fun checkRegistrationAgreement(eventId : String) : Single<RegistrationAgreementStatus>
    fun acceptRegistrationAgreement(eventId : String) : Single<RegistrationAgreementStatus>
}