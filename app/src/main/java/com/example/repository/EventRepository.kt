package com.example.repository

import com.example.data.bodies.AddToFavoriteModel
import com.example.data.bodies.EventCalendarBody
import com.example.data.bodies.MessageToEventBody
import com.example.data.bodies.RegisterToEventBody
import com.example.data.models.*
import com.example.data.models.user.User
import com.example.util.pagination.PaginationResponse
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import okhttp3.RequestBody
import retrofit2.http.*

interface EventRepository {
    //fun getEventList(limit: Int, offset: Int, filter: Map<String, Any>? = null): Maybe<PaginationResponse<Event?>>
    //fun getEventRecommendations(limit: Int, offset: Int): Maybe<PaginationResponse<Event?>>
    //fun getEventRegisterField(eventId: String): Single<EventRegisterForm>
    //fun eventRegister(eventId: String, body: RequestBody): Single<EventRegisterResponse>
    //fun eventRegisterCheck(eventId: String): Single<List<EventRegisterCheckField>>
    //fun eventRegisterCancel(eventId: String): Completable
    //fun getEventRegister(eventId: String): Single<EventRegisterResponse>
    //fun getEventRatingForm(eventId: String): Single<List<EventRegisterField>>
    fun setEventRating(eventId: String, body: RequestBody): Completable
    //fun getEventRating(eventId: String): Single<EventInfo>
    //fun getEventActivity(eventId: String): Maybe<EventActivity>
    //fun getEventInfo(eventId: String): Maybe<EventInfo>
    //fun setDefaultEvent(eventId: String): Completable
    //fun addEventToCalendar(eventId: String, subEventId: String): Completable
    //fun removeEventFromCalendar(eventId: String, subEventId: String): Completable
    //fun getPartnerListByEvent(eventId: String): Single<List<Partner>>
    //fun getPartnerById(eventId: String, partnerId: String): Single<Partner>
    //fun getSubevent(eventId: String, subEventId: String): Single<SubeventInfo>
    //fun getSubeventUsers(eventId: String, subEventId: String, limit: Int, offset: Int): Maybe<PaginationResponse<User>>
    //fun getCategoriesList(): Single<List<EventGroup>>
    //fun getEventSpeakers(eventId: String, limit: Int, offset: Int): Maybe<PaginationResponse<Speaker>>
    //fun getEventByCode(code: String): Single<QrEvent>
    //fun getPage(event: String, page: String): Single<Page>

    //fun loadEventRegistrationData(eventId: String): Single<EventRegisterData>
    fun loadEventRatingData(eventId: String): Single<EventRatingData>

    //fun getFavoriteEvents(limit: Int, offset: Int): Maybe<PaginationResponse<Event?>>
    //fun addToFavorite(eventId: String): Completable
    //fun removeFromFavorite(eventId: String): Completable

    //fun sendMessageToOrganization(eventId: String, message: String): Completable

    //fun subscribeToSubevent(event: String, activity: String): Completable
    //fun unsubscribeFromSubEvent(event: String, activity: String): Completable

    //Alfa API
    fun getEventsList(map: Map<String, Any>): Maybe<PaginationResponse<EventNew?>>
    fun getEventsListWithoutPagination(map: Map<String, Any>): Maybe<EventNewModelWithoutPagination>
    fun getEventFormatsList(map: Map<String, Any>): Maybe<List<NewEventFormat>>
    fun getEventDetails(eventId: String): Maybe<EventInfo>
    fun getEventDetailForRegister(eventId: String): Maybe<EventNew>
    fun mailToEvent(body: MessageToEventBody): Completable
    fun getPageDetails(pageId: String): Single<PageModel>
    fun getSpeakers(map: Map<String, Any>): Maybe<PaginationResponse<MemberModel>>
    fun getPartnerDetails(partnerId : String): Single<PartnerModel>
    fun addToFavorites(body: AddToFavoriteModel): Single<AddFavoriteModel>
    fun deleteFromFavorite(id : String): Completable
    fun checkUserProfile(): Maybe<UserProfileFieldsModel>
    fun getEventFavoritesList(map: Map<String, Any>): Maybe<PaginationResponse<EventNew?>>

    //fun loadEventRegistrationDataNew(eventId: String): Single<EventRegisterData>
    fun getEventForm(map: Map<String, Any>): Single<ApiNewResponse<List<EventFormModel/*EventRegisterField*/>>>
    fun getEventFormResult(map: Map<String, Any>): Single<ApiNewResponse<List<EventFormResultModel>>>
    fun eventRegisterNew(body: RequestBody): Single<ApiResponse<List<EventFormResultModel>>>
    fun registerToEvent(eventId : Int): Completable
    fun cancelRegisterToEvent(eventId : Int): Completable
    fun addEventToCalendarWithResult(body: EventCalendarBody): Single<EventCalendarItem>
    fun addEventToCalendar(body: EventCalendarBody): Completable
    fun deleteAllCalendarEvents(entityType: String): Completable
    fun deleteCalendarEvent(id: String): Completable
    fun getUserCalendarEvent( entityType: String): Maybe<ApiNewResponse<List<EventCalendarItem>>>
    fun getEventActivities(eventId: Int): Maybe<List<EventActivityModel>>
    fun getEventActivityDetail(activityId: String): Single<EventActivityModel>
    fun getTags(map: Map<String, Any>): Maybe<List<EventTagModel>>
}