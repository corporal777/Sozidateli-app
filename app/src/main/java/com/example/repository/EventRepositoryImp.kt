package com.example.repository

import android.util.Log
import com.example.api.Api
import com.example.api.NewApi
import com.example.data.AppData
import com.example.data.bodies.AddToFavoriteModel
import com.example.data.bodies.EventCalendarBody
import com.example.data.bodies.MessageToEventBody
import com.example.data.bodies.RegisterToEventBody
import com.example.data.models.*
import com.example.ui.event.registration.items.ProfileFieldsData
import com.example.ui.event.registration.items.ProfileFieldsFormModel
import com.example.util.pagination.PaginationResponse
import com.google.gson.JsonElement
import fromJson
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import okhttp3.RequestBody
import javax.inject.Inject

class EventRepositoryImp
@Inject constructor(
    private val api: Api,
    private val newApi: NewApi,
    val appData: AppData
) : ApiRepository(appData), EventRepository {

    /*override fun getEventList(limit: Int, offset: Int, filter: Map<String, Any>?): Maybe<PaginationResponse<Event?>> {
        return callPagination(api.getEventList(limit, offset, filter))
    }

    override fun getEventRecommendations(limit: Int, offset: Int): Maybe<PaginationResponse<Event?>> {
        return callPagination(api.getEventRecommendations(limit, offset))
    }

    override fun getEventRegisterField(eventId: String): Single<EventRegisterForm> {
        return call(api.getEventRegisterField(eventId))
    }

    override fun eventRegister(eventId: String, body: RequestBody): Single<EventRegisterResponse> {
        return call(api.eventRegister(eventId, body))
    }

    override fun eventRegisterCheck(eventId: String): Single<List<EventRegisterCheckField>> {
        return call(api.eventRegisterCheck(eventId))
                .map {
                    it.fields?.filter { field ->
                        !field.filled
                    } ?: emptyList()
                }
    }

    override fun eventRegisterCancel(eventId: String): Completable {
        return call(api.eventRegisterCancel(eventId))
    }

    override fun getEventRegister(eventId: String): Single<EventRegisterResponse> {
        return call(api.getEventRegister(eventId))
    }

    override fun getEventRatingForm(eventId: String): Single<List<EventRegisterField>> {
        return call(api.getEventRatingForm(eventId))
    }

    override fun getEventActivity(eventId: String): Maybe<EventActivity> {
        return call(api.getEventActivity(eventId))
    }

    override fun getEventInfo(eventId: String): Maybe<EventInfo> {
        return call(api.getEventInfo(eventId))
    }

    override fun setDefaultEvent(eventId: String): Completable {
        return call(api.setDefaultEvent(eventId))
    }

    override fun addEventToCalendar(eventId: String, subEventId: String): Completable {
        return call(api.addSubEventToCalendar(eventId, subEventId))
    }

    override fun removeEventFromCalendar(eventId: String, subEventId: String): Completable {
        return call(api.removeSubEventFromCalendar(eventId, subEventId))
    }

    override fun getPartnerListByEvent(eventId: String): Single<List<Partner>> {
        return call(api.getPartnerListByEvent(eventId))
    }

    override fun getPartnerById(eventId: String, partnerId: String): Single<Partner> {
        return call(api.getPartnerById(eventId, partnerId))
    }

    override fun getSubevent(eventId: String, subEventId: String): Single<SubeventInfo> {
        return call(api.getSubEvent(eventId, subEventId))
    }

    override fun getSubeventUsers(eventId: String, subEventId: String, limit: Int, offset: Int): Maybe<PaginationResponse<User>> {
        return callPagination(api.getSubEventUsers(eventId, subEventId, limit, offset))
    }

    override fun getCategoriesList(): Single<List<EventGroup>> {
        return call(api.getCategoriesList())
    }

    override fun getEventSpeakers(eventId: String, limit: Int, offset: Int): Maybe<PaginationResponse<Speaker>> {
        return callPagination(api.getEventSpeakers(eventId, limit, offset))
    }

    override fun setEventRating(eventId: String, body: RequestBody): Completable {
        return call(api.setEventRating(eventId, body))
    }

    override fun getEventRating(eventId: String): Single<EventInfo> {
        return call(api.getEventRating(eventId))
    }

    override fun getEventByCode(code: String): Single<QrEvent> {
        return call(api.getEventByCode(code))
    }

    override fun getPage(event: String, page: String): Single<Page> {
        return call(api.getEventPage(event, page))
    }

    override fun loadEventRegistrationData(eventId: String): Single<EventRegisterData> {
        val loadFields = getEventRegisterField(eventId)
        val loadRegister = getEventRegister(eventId)

        return Single.zip(
                loadFields,
                loadRegister,
                BiFunction<EventRegisterForm, EventRegisterResponse, EventRegisterData> { fields, registration ->
                    val fieldsData = createFieldsData(fields.fields, registration.fields)
                    val group: EventRegisterField? = fields.fields?.find {
                        it.type == EventRegisterField.Type.GROUP
                    }

                    EventRegisterData(
                            registration.event,
                            group,
                            registration.group_id,
                            fields.groups ?: emptyList(),
                            fieldsData ?: emptyList()
                    )
                }
        )
    }

    override fun loadEventRegistrationDataNew(eventId: String): Single<EventRegisterData> {
        val loadFields = getEventRegisterField(eventId)
        val loadRegister = getEventRegister(eventId)
    }*/

    /*override fun loadEventRatingData(eventId: String): Single<EventRatingData> {
        return Single.just(EventRatingData(EventData("","", null,null,null,null,
                "",null,null,null,null,null,
                null,null,null,null,
                null,null,null,null,null,null,null
                ,null,null,null, arrayListOf(), arrayListOf()
                , arrayListOf(), arrayListOf(),null,null,null
                ,null,null,null,null,null
                ,false,false,null), arrayListOf(), null, null))
        return Single.zip(
                getEventRating(eventId),
                getEventRatingForm(eventId),
                BiFunction<EventInfo, List<EventRegisterField>, EventRatingData> { eventInfo, fields ->
                    val fieldsData = createFieldsData(fields, eventInfo.responseFields)
                    return@BiFunction EventRatingData(
                            event = eventInfo.event,
                            fieldsData = fieldsData ?: emptyList(),
                            ratingValue = eventInfo.ratingValue
                    )
                }
        )
    }*/

    private fun createFieldsData(
        fields: List<EventRegisterField>?,
        responseField: List<EventRegisterResponseField?>?
    ): List<EventRegisterFieldData<*>>? {
        return fields?.mapNotNull { field ->
            when (field.type) {
                EventRegisterField.Type.STRING,
                EventRegisterField.Type.TEXT_AREA,
                EventRegisterField.Type.NUMBER -> EventRegisterFieldData.String(
                    field,
                    findRegistrationDataValue(field, responseField).fromJson<String>()
                )
                EventRegisterField.Type.DATE,
                EventRegisterField.Type.DATETIME -> EventRegisterFieldData.Date(
                    field,
                    findRegistrationDataValue(field, responseField).fromJson<String>()
                )
                EventRegisterField.Type.CHECKBOX -> EventRegisterFieldData.Checkbox(
                    field,
                    findRegistrationDataValue(field, responseField).fromJson<Set<String>>()
                )
                EventRegisterField.Type.SELECT_BOX -> EventRegisterFieldData.SelectBox(
                    field,
                    findRegistrationDataValue(field, responseField).fromJson<String>()
                )
                EventRegisterField.Type.RADIO_BOX -> EventRegisterFieldData.RadioBox(
                    field,
                    findRegistrationDataValue(field, responseField).fromJson<String>()
                )
                EventRegisterField.Type.FILE -> EventRegisterFieldData.File(
                    field,
                    findRegistrationDataValue(
                        field,
                        responseField
                    ).fromJson(EventFile.Deserializer())
                )
                EventRegisterField.Type.BOOLEAN -> EventRegisterFieldData.Boolean(
                    field,
                    findRegistrationDataValue(field, responseField).fromJson<Boolean>()
                )
                EventRegisterField.Type.PASSPORT -> EventRegisterFieldData.Passport(
                    field,
                    findRegistrationDataValue(field, responseField).fromJson<EventPassport>()
                )
                else -> null
            }
        }
    }

    private fun findRegistrationDataValue(
        field: EventRegisterField,
        fields: List<EventRegisterResponseField?>?
    ): JsonElement? {
        return fields?.find { field.id == it?.id }?.value
    }

    /*override fun addToFavorite(eventId: String): Completable {
        return call(api.subscribeToEvent(eventId))
    }

    override fun removeFromFavorite(eventId: String): Completable {
        return call(api.unsubscribeFromEvent(eventId))
    }

    override fun getFavoriteEvents(limit: Int, offset: Int): Maybe<PaginationResponse<Event?>> {
        return callPagination(api.getFavoriteEvents(limit, offset))
    }

    override fun sendMessageToOrganization(eventId: String, message: String): Completable {
        return call(api.sendMessageToOrganization(eventId, message))
    }

    override fun subscribeToSubevent(event: String, activity: String): Completable {
        return call(api.subscrbeToSubevent(event, activity))
    }

    override fun unsubscribeFromSubEvent(event: String, activity: String): Completable {
        return call(api.unsubscribeFromSubEvent(event, activity))
    }*/

    //Alfa API
    override fun getEventsList(map: Map<String, Any>): Maybe<PaginationResponse<EventNew?>> =
        newApi.getEventsList(map)
            .map { PaginationResponse(it.totalCount, it.data ?: arrayListOf()) }

    override fun getEventsListFlow(map: Map<String, Any>): Flowable<PaginationResponse<EventNew?>> {
        return newApi.getEventsListFlow(map)
            .map {
                val eventFormats = appData.getEventFormats()
                if (!eventFormats.isNullOrEmpty()) {
                    it.data?.forEach { ev ->
                        ev?.format?.name =
                            eventFormats.firstOrNull { f -> f.id == ev?.format?.value }?.name
                    }
                }
                PaginationResponse(it.totalCount, it.data ?: arrayListOf())
            }
    }

    override fun getSortedEventsList(map: Map<String, Any>): Maybe<PaginationResponse<EventNew?>> =
        newApi.getSortedEventsList(map)
            .map {
                val eventFormats = appData.getEventFormats()
                if (!eventFormats.isNullOrEmpty()) {
                    it.data?.forEach { ev ->
                        ev?.format?.name =
                            eventFormats.firstOrNull { f -> f.id == ev?.format?.value }?.name
                    }
                }
                PaginationResponse(it.totalCount, it.data ?: arrayListOf())
            }

    override fun getOrganizationEventsList(map: Map<String, Any>): Maybe<PaginationResponse<EventNew?>> {
        return newApi.getOrganizationEventsList(map)
            .map {
                val eventFormats = appData.getEventFormats()
                if (!eventFormats.isNullOrEmpty()) {
                    it.data?.forEach { ev ->
                        ev?.format?.name =
                            eventFormats.firstOrNull { f -> f.id == ev?.format?.value }?.name
                    }
                }
                PaginationResponse(it.totalCount, it.data ?: arrayListOf())
            }
    }

    override fun getEventsListWithoutPagination(map: Map<String, Any>): Maybe<EventNewModelWithoutPagination> =
        newApi.getEventsListWithoutPagination(map)
            .map {
                val eventFormats = appData.getEventFormats()
                if (!eventFormats.isNullOrEmpty()) {
                    it.data?.forEach { ev ->
                        ev?.format?.name =
                            eventFormats.firstOrNull { f -> f.id == ev?.format?.value }?.name
                    }
                }
                it
            }

    override fun getOrganizationEventsListWithoutPagination(map: Map<String, Any>): Maybe<EventNewModelWithoutPagination> {
        return newApi.getOrganizationEventsListWithoutPagination(map)
            .map {
                val eventFormats = appData.getEventFormats()
                if (!eventFormats.isNullOrEmpty()) {
                    it.data?.forEach { ev ->
                        ev?.format?.name =
                            eventFormats.firstOrNull { f -> f.id == ev?.format?.value }?.name
                    }
                }
                it
            }
    }


    override fun getEventFormatsList(map: Map<String, Any>): Maybe<List<NewEventFormat>> {
        val eventFormats = appData.getEventFormats()
        return if (eventFormats.isNullOrEmpty())
            newApi.getEventFormatsList(map)
                .doOnSuccess {
                    appData.setEventFormats(it.data)
                }.map { it.data }
        else
            Maybe.just(eventFormats)
    }

    override fun getActiveEventFormatsList(): Maybe<List<NewEventFormat>> {
        return newApi.getActiveEventFormatsList().map { it.data }
    }

    override fun getEventDetails(eventId: String): Maybe<EventInfo> =
        newApi.getEventDetails(
            eventId,
            "rights,organization,organization.userFavorite,tag,page,format,activity,activity.userCalendar,activity.auditorium,user-registration,user-form-result,partner,member,member.user,userFavorite,current-user-registration,destination-scheme,eventRegistrationState,current-user-registration-state,is-user-subscribed,event-subscribe"
        )
            .map {
                val eventFormats = appData.getEventFormats()
                if (!eventFormats.isNullOrEmpty()) {
                    it.format?.name =
                        eventFormats.firstOrNull { f -> f.id == it.format?.value }?.name
                }
                EventInfo(
                    it,
                    it.binds?.partner ?: arrayListOf(),
                    it.binds?.page ?: arrayListOf(),
                    if (it.binds?.userRegister?.isNotEmpty() == true) it.binds?.userRegister?.get(0) else null,
                    it.state?.rating?.askDelay,
                    it.binds?.form
                )

            }

    override fun getEventMember(memberId: String): Maybe<MemberModel> =
        newApi.getEventMember(
            memberId,
            "user,user.userFavorite,user.chat-room-with-me,activities,activity.userCalendar"
        )

    override fun createEventSubscription(eventId: Int): Completable =
        newApi.createEventSubscription(EventSubscriptionRequest(eventId))

    override fun deleteEventSubscription(eventId: Int): Completable =
        newApi.deleteEventSubscription(eventId)

    override fun getEventDetailForRegister(eventId: String): Maybe<EventNew> =
        newApi.getEventDetails(eventId, "rights,current-user-registration,form,organization")

    override fun mailToEvent(body: MessageToEventBody): Completable =
        newApi.messageToEvent(body)

    override fun getPageDetails(pageId: String): Single<PageModel> =
        newApi.getPageDetails(pageId)

    override fun getSpeakers(map: Map<String, Any>): Maybe<PaginationResponse<MemberModel>> =
        newApi.getSpeakers(map)
            .map {
                PaginationResponse(
                    it.totalCount,
                    it.data
                )
            }

    override fun getSpeakersWithoutPagination(map: Map<String, Any>): Maybe<List<MemberModel>> =
        newApi.getSpeakers(map)
            .map {
                it.data
            }

    override fun getUserCalendarEvents(): Maybe<List<EventNew>?> =
        newApi.getUserCalendarEvents("activity.userCalendar,activity.auditorium,activity.member")
            .map {
                it.data
            }

    override fun getPartnerDetails(partnerId: String): Single<PartnerModel> =
        newApi.getPartnerDetails(partnerId, "event")

    override fun addToFavorites(body: AddToFavoriteModel): Single<AddFavoriteModel> =
        newApi.addToFavorite(body)

    override fun deleteFromFavorite(id: String): Completable =
        newApi.deleteFromFavorite(id)

    override fun checkUserProfile(): Maybe<UserProfileFieldsModel> =
        newApi.checkUserProfile(appData.getId().toString())

    override fun getEventFavoritesList(map: Map<String, Any>): Maybe<PaginationResponse<EventNew?>> {
        return newApi.getEventFavoritesList(map)
            .map {
                it.data.forEach { org ->
                    org.entity?.model?.binds?.userFavorite =
                        EventUserFavorite(org.id?.toLong(), org.user)
                }
                PaginationResponse(it.totalCount, it.data.map { org -> org.entity?.model })
            }
    }

    override fun getEventForm(map: Map<String, Any>): Single<ApiNewResponse<List<EventFormModel/*EventRegisterField*/>>> =
        newApi.getEventForm(map)/*.map {
                val result = mutableListOf<EventRegisterField>()
                it.data[0].fields?.forEach { field ->
                    result.add(EventRegisterField(field.id.toString(), field.name, field.sort?: 0,
                    field.type?: EventRegisterField.Type.STRING, field.isRequired,
                    field.description, null, null, null, null))
                }
                ApiNewResponse(result.toList() , it.totalCount)
            }*/

    override fun getEventFormResult(map: Map<String, Any>): Single<ApiNewResponse<List<EventFormResultModel>>> =
        newApi.getEventFormResult(map)

    //+
    override fun getEventFormResultDraft(
        id: Int,
        map: Map<String, Any>
    ): Maybe<EventFormResultDraftModel> {
        return newApi.getEventFormResultDraft(id, map)
    }

    override fun saveEventFormResultDraft(body: RequestBody): Single<EventFormResultModel> =
        newApi.sendEventFormResultForRegister(body)

    override fun eventRegisterNew(body: RequestBody): Single<ApiNewResponse<List<EventFormResultModel>>> =
        newApi.eventRegister(body)

    //+
    override fun loadEventFormResult(id: String): Single<ProfileFieldsData> {
        return newApi.loadProfileFieldsFormResult(id)
    }

    override fun registerToEvent(eventId: Int): Completable =
        newApi.registerToEvent(eventId, RegisterToEventBody(appData.getId()))

    override fun cancelRegisterToEvent(eventId: Int): Completable =
        newApi.cancelRegisterToEvent(eventId)

    override fun addEventToCalendar(body: EventCalendarBody): Completable =
        newApi.addEventToCalendar(body).doOnComplete {
            appData.defaultEvent = body.entity.id
        }

    override fun addEventToCalendarWithResult(body: EventCalendarBody): Single<EventCalendarItem> =
        newApi.addEventToCalendarWithResult(body).doOnSuccess {
            appData.defaultEvent = body.entity.id
        }

    override fun deleteAllCalendarEvents(entityType: String): Completable =
        newApi.deleteAllCalendarEvents(appData.getId(), entityType).doOnComplete {
            appData.defaultEvent = null
        }


    override fun deleteCalendarEvent(id: String): Completable =
        newApi.deleteCalendarEvent(id)

    override fun getUserCalendarEvent(entityType: String): Maybe<ApiNewResponse<List<EventCalendarItem>>> =
        newApi.getUserCalendarEvent(appData.getId(), entityType)

    override fun getEventActivities(eventId: Int): Maybe<List<EventActivityModel>> =
        newApi.getEventActivities(
            eventId,
            "event,member,tag,auditorium,userCalendar,member.user,member.user.userFavorite,userFavorite",
            "holdingDate.from",
            "asc"
        )
            .doOnSuccess { it }.map { it.data }

    override fun getEventActivityDetail(activityId: String): Single<EventActivityModel> =
        newApi.getEventActivity(
            activityId,
            "event,member,auditorium,tag,userCalendar,member.user.userFavorite,member.user,userFavorite"
        )

    override fun getTags(map: Map<String, Any>): Maybe<List<EventTagModel>> =
        newApi.getTags(map).map { it.data }

    override fun searchEventsNew(map: Map<String, Any>): Maybe<PaginationResponse<EventNew?>> {
        return newApi.searchDataNew(map)
            .map { PaginationResponse(it.events.count, it.events.data) }
    }


    override fun checkRegistrationAgreement(eventId: String): Maybe<RegistrationAgreementStatus> {
        return newApi.checkRegistrationAgreement(eventId.toInt())
    }

    override fun acceptRegistrationAgreement(eventId: String): Maybe<RegistrationAgreementStatus> {
        return newApi.acceptRegistrationAgreement(eventId.toInt())
    }
}