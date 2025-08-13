package com.examle.data.repository

import com.examle.data.AppData
import com.examle.data.bodies.AddToFavoriteEntityModel
import com.examle.data.bodies.AddToFavoriteEntityModel.Companion.FAVORITE_EVENT
import com.examle.data.bodies.AddToFavoriteModel
import com.examle.data.bodies.RegisterToEventBody
import com.examle.data.mapper.mapToDomainEventDetailModel
import com.examle.data.mapper.mapToDomainEventModel
import com.examle.data.mapper.mapToPagingDomainModel
import com.examle.data.models.AddFavoriteModel
import com.examle.data.source.remote.Api
import com.examle.domain.model.FavoriteModel
import com.examle.domain.model.PaginationResponse
import com.examle.domain.model.event.EventModel
import com.examle.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class EventRepositoryImp
@Inject constructor(
    private val api: Api,
    private val appData: AppData
) : ApiRepository(appData), EventRepository {


    override suspend fun getEventsList(map: Map<String, Any>): PaginationResponse<EventModel> {
        return api.getEventsList(map).await().mapToPagingDomainModel(appData.isTemporaryUser())
    }

    override fun getEvent(eventId: String, binds: String): Flow<EventModel> {
        return flow { emit(api.getEventDetails(eventId, binds)) }
            .map { it.mapToDomainEventModel(appData.isTemporaryUser()) }
    }

    override fun acceptEventAgreement(eventId: Int): Flow<String> {
        return flow { emit(api.acceptEventAgreement(eventId)) }.map { it.status }
    }

    override suspend fun registerEvent(eventId: Int) {
        return api.registerEvent(eventId, RegisterToEventBody(appData.getId()))
    }

    override suspend fun cancelRegisterEvent(eventId: Int) {
        return api.cancelRegisterEvent(eventId)
    }



    override fun getEventDetail(eventId: String, binds: String): Flow<EventModel> {
        return flow {
            emit(
                api.getEventDetails(
                    eventId,
                    "organization,organization.userFavorite,tag,page,format,activity," +
                            "activity.userCalendar,activity.auditorium,partner,member," +
                            "userFavorite,destination-scheme,is-user-subscribed,event-subscribe," +
                            "user-form-result," + binds
                )
            )
        }.map { it.mapToDomainEventDetailModel(appData.isTemporaryUser()) }
    }

    override fun addEventToFavorites(eventId: String): Flow<FavoriteModel> {
        return if (appData.isTemporaryUser())
            flow {
                val body = AddToFavoriteModel(
                    appData.getTempId(),
                    AddToFavoriteEntityModel(FAVORITE_EVENT, eventId.toInt())
                )
                emit(api.addToTempFavorite(body))
            }.map { FavoriteModel(it.id, it.tempUser) }
        else flow {
            val body = AddToFavoriteModel(
                appData.getId(),
                AddToFavoriteEntityModel(FAVORITE_EVENT, eventId.toInt())
            )
            emit(api.addToFavorite(body))
        }.map { FavoriteModel(it.id, it.user) }
    }

    override fun removeEventFromFavorites(id: String) : Flow<Unit>{
        return if (appData.isTemporaryUser()) flow { emit(api.deleteFromTempFavorite(id)) }
        else flow { emit(api.deleteFromFavorite(id)) }
    }

//    //Alfa API
//    override fun getEventByCode(code: String): Single<EventNew> {
//        return api.getEventsList(
//            mapOf(
//                EventNew.EVENT_LIMIT to 1,
//                EventNew.EVENT_OFFSET to 0,
//                EventNew.EVENT_CODE to code
//            )
//        ).flatMapSingle {
//            if (it.data.isNullOrEmpty()) Single.error(NullPointerException())
//            else Single.just(it.data.first())
//        }
//    }
//


//
//    override fun getEventDetails(eventId: String): Maybe<EventNew> =
//        getEventFormatsList(mapOf(EventNew.EVENT_LIMIT to 100, EventNew.EVENT_OFFSET to 0))
//            .flatMap {
//                api.getEventDetails(
//                    eventId,
//                    "organization,organization.userFavorite,tag,page,format,activity," +
//                            "activity.userCalendar,activity.auditorium,partner,member,member.user," +
//                            "userFavorite,destination-scheme,is-user-subscribed,event-subscribe," +
//                            "user-form-result," + getCurrentRegistrationBinds()
//                ).map {
//                    val formats = appData.getEventFormats()
//                    if (!formats.isNullOrEmpty()) {
//                        it.format?.name =
//                            formats.firstOrNull { f -> f.id == it.format?.value }?.name
//                    }
//                    it
//                }
//            }
//
//
//    override fun getEventDetailForRegister(eventId: String): Maybe<EventRegisterData> =
//        api.getEventDetails(eventId, "user-form-result")
//            .flatMap { e ->
//                val fields = e.binds?.getForm()?.fields ?: emptyList()
//                val results = e.binds?.getFormResult()?.fields?.toMutableList() ?: mutableListOf()
//                val pref = fields.find { x -> x.type == EventFormFieldModel.Type.PREFILLED }
//
//                if (pref == null) Maybe.just(EventRegistration.createData(e, fields, results))
//                else if (results.none { it.id == pref.id }) {
//                    getPrefilledEventFormResult(pref.id.toString()).flatMapMaybe { r ->
//                        results.add(
//                            EventFormResultFieldModel(
//                                r.form,
//                                null,
//                                r.fields.prefilledToJson()
//                            )
//                        )
//                        Maybe.just(EventRegistration.createData(e, fields, results))
//                    }
//                } else Maybe.just(EventRegistration.createData(e, fields, results))
//            }.map {
//                val data = EventRegisterField.createFieldsData(it.formFields, it.formResult)
//                EventRegisterData(it).addFields(data)
//            }
//
//
//    override fun getEventsList(map: Map<String, Any>): Maybe<com.examle.data.response.PaginationResponse<EventNew?>> =
//        api.getEventsList(map)
//            .map {
//                com.examle.data.response.PaginationResponse(
//                    it.totalCount,
//                    it.data ?: arrayListOf()
//                )
//            }
//
//
//
//    override fun getSortedEventsList(map: Map<String, Any>): Maybe<com.examle.data.response.PaginationResponse<EventNew>> =
//        api.getSortedEventsList(map)
//            .map {
//                com.examle.data.response.PaginationResponse(
//                    it.totalCount,
//                    it.data ?: arrayListOf()
//                )
//            }
//
//    override fun getOrganizationEventsList(map: Map<String, Any>): Maybe<com.examle.data.response.PaginationResponse<EventNew>> {
//        return api.getOrganizationEventsList(map)
//            .map {
//                com.examle.data.response.PaginationResponse(
//                    it.totalCount,
//                    it.data ?: arrayListOf()
//                )
//            }
//    }
//
//
//    override fun getEventFormatsList(map: Map<String, Any>): Maybe<List<NewEventFormat>> {
//        val eventFormats = appData.getEventFormats()
//        return if (eventFormats.isNullOrEmpty())
//            api.getEventFormatsList(map)
//                .doOnSuccess { appData.setEventFormats(it.data) }.map { it.data }
//        else Maybe.just(eventFormats)
//    }
//
//    override fun getActiveEventFormatsList(): Maybe<List<NewEventFormat>> {
//        return api.getActiveEventFormatsList().map { it.data }
//    }
//
//
//    override fun getEventMember(memberId: String): Maybe<MemberModel> =
//        api.getEventMember(
//            memberId,
//            "user,user.userFavorite,user.chat-room-with-me,activities,activity.userCalendar"
//        )
//
//    override fun createEventSubscription(eventId: Int): Completable =
//        api.createEventSubscription(EventSubscriptionRequest(eventId))
//
//    override fun deleteEventSubscription(eventId: Int): Completable =
//        api.deleteEventSubscription(eventId)
//
//    override fun mailToEvent(body: MessageToEventBody): Completable =
//        api.messageToEvent(body)
//
//    override fun getPageDetails(pageId: String): Single<PageModel> =
//        api.getPageDetails(pageId)
//
//    override fun getSpeakers(map: Map<String, Any>): Maybe<com.examle.data.response.PaginationResponse<MemberModel>> =
//        api.getSpeakers(map)
//            .map {
//                com.examle.data.response.PaginationResponse(
//                    it.totalCount,
//                    it.data
//                )
//            }
//
//    override fun getSpeakersWithoutPagination(map: Map<String, Any>): Maybe<List<MemberModel>> =
//        api.getSpeakers(map)
//            .map {
//                it.data
//            }
//
//    override fun getUserCalendarEvents(): Maybe<List<EventNew>?> =
//        api.getUserCalendarEvents("activity.userCalendar,activity.auditorium,activity.member")
//            .map {
//                it.data
//            }
//
//    override fun getPartnerDetails(partnerId: String): Single<PartnerModel> =
//        api.getPartnerDetails(partnerId, "event")
//
//    override fun addToFavorites(body: com.examle.data.bodies.AddToFavoriteModel): Single<AddFavoriteModel> =
//        api.addToFavorite(body)
//

//

//
//
//    override fun addOrgToFavorites(orgId: String): Single<AddFavoriteModel> {
//        return if (appData.isTemporaryUser()) api.addToTempFavorite(
//            com.examle.data.bodies.AddToFavoriteModel(
//                appData.getTempId(),
//                com.examle.data.bodies.AddToFavoriteEntityModel(
//                    com.examle.data.bodies.AddToFavoriteEntityModel.FAVORITE_ORGANIZATION,
//                    orgId.toInt()
//                )
//            )
//        ).map { AddFavoriteModel(it.id, it.tempUser) }
//        else api.addToFavorite(
//            com.examle.data.bodies.AddToFavoriteModel(
//                appData.getId(),
//                com.examle.data.bodies.AddToFavoriteEntityModel(
//                    com.examle.data.bodies.AddToFavoriteEntityModel.FAVORITE_ORGANIZATION,
//                    orgId.toInt()
//                )
//            )
//        )
//    }
//
//    override fun addUserToFavorites(speakerId: String): Single<AddFavoriteModel> {
//        return if (appData.isTemporaryUser()) api.addToTempFavorite(
//            com.examle.data.bodies.AddToFavoriteModel(
//                appData.getTempId(),
//                com.examle.data.bodies.AddToFavoriteEntityModel(
//                    com.examle.data.bodies.AddToFavoriteEntityModel.FAVORITE_SPEAKER,
//                    speakerId.toInt()
//                )
//            )
//        ).map { AddFavoriteModel(it.id, it.tempUser) }
//        else api.addToFavorite(
//            com.examle.data.bodies.AddToFavoriteModel(
//                appData.getId(),
//                com.examle.data.bodies.AddToFavoriteEntityModel(
//                    com.examle.data.bodies.AddToFavoriteEntityModel.FAVORITE_SPEAKER,
//                    speakerId.toInt()
//                )
//            )
//        )
//    }
//
//
//    override fun checkUserProfile(): Maybe<UserProfileFieldsModel> =
//        api.checkUserProfile(appData.getId())
//
//    override fun getEventFavoritesList(map: Map<String, Any>): Maybe<com.examle.data.response.PaginationResponse<EventNew>> {
//        return api.getEventFavoritesList(map)
//            .map {
//                it.data.forEach { org ->
//                    org.entity?.model?.binds?.userFavorite =
//                        EventUserFavorite(org.id?.toLong(), org.user)
//                }
//                com.examle.data.response.PaginationResponse(
//                    it.totalCount,
//                    it.data.mapNotNull { org -> org.entity?.model })
//            }
//    }
//
//    override fun getEventForm(map: Map<String, Any>): Single<ApiNewResponse<List<EventFormModel/*EventRegisterField*/>>> =
//        api.getEventForm(map)/*.map {
//                val result = mutableListOf<EventRegisterField>()
//                it.data[0].fields?.forEach { field ->
//                    result.add(EventRegisterField(field.id.toString(), field.name, field.sort?: 0,
//                    field.type?: EventRegisterField.Type.STRING, field.isRequired,
//                    field.description, null, null, null, null))
//                }
//                ApiNewResponse(result.toList() , it.totalCount)
//            }*/
//
//    override fun getEventFormResult(map: Map<String, Any>): Single<ApiNewResponse<List<EventFormResultModel>>> =
//        api.getEventFormResult(map)
//
//    //+
//    override fun getEventFormResultDraft(
//        id: Int,
//        map: Map<String, Any>
//    ): Maybe<EventFormResultDraftModel> {
//        return api.getEventFormResultDraft(id, map)
//    }
//
//    override fun saveEventFormResultDraft(body: RequestBody): Single<EventFormResultModel> =
//        api.sendEventFormResultForRegister(body)
//
//    override fun sendFormToRegister(body: RequestBody): Single<ApiNewResponse<List<EventFormResultModel>>> =
//        api.eventRegister(body)
//
//    //+
//    override fun getPrefilledEventFormResult(id: String): Single<EventRegisterProfilePrefilledData> {
//        return api.getPrefilledEventFormResult(id)
//    }
//
//    override fun registerToEvent(eventId: Int): Completable =
//        api.registerToEvent(eventId, RegisterToEventBody(appData.getId()))
//
//
//
//    override fun cancelRegisterToEvent(eventId: Int): Completable =
//        api.cancelRegisterToEvent(eventId)
//
//    override fun addEventToCalendar(body: com.examle.data.bodies.EventCalendarBody): Completable =
//        api.addEventToCalendar(body)
//
//    override fun addEventToCalendarWithResult(body: com.examle.data.bodies.EventCalendarBody): Single<EventCalendarModel> =
//        api.addEventToCalendarWithResult(body)
//
//    override fun deleteAllCalendarEvents(entityType: String): Completable =
//        api.deleteAllCalendarEvents(appData.getId(), entityType)
//
//
//    override fun deleteCalendarEvent(id: String): Completable =
//        api.deleteCalendarEvent(id)
//
//    override fun getUserCalendarEvent(entityType: String): Maybe<ApiNewResponse<List<EventCalendarModel>>> =
//        api.getUserCalendarEvent(appData.getId(), entityType)
//
//    override fun getEventActivities(eventId: Int): Maybe<List<EventActivityModel>> =
//        api.getEventActivities(
//            eventId,
//            "event,member,tag,auditorium,userCalendar,member.user,member.user.userFavorite,userFavorite",
//            "holdingDate.from",
//            "asc"
//        ).map { it.data }
//
//    override fun getEventActivityDetail(activityId: String): Single<EventActivityModel> =
//        api.getEventActivity(
//            activityId,
//            "event,member,auditorium,tag,userCalendar,member.user.userFavorite,member.user,userFavorite"
//        )
//
//    override fun getTags(map: Map<String, Any>): Maybe<List<EventTagModel>> =
//        api.getTags(map).map { it.data }
//
//    override fun searchEvents(map: Map<String, Any>): Maybe<com.examle.data.response.PaginationResponse<EventNew>> {
//        return api.searchGlobal(map)
//            .map { com.examle.data.response.PaginationResponse(it.events.count, it.events.data) }
//    }
//
//
//    override fun checkRegistrationAgreement(eventId: String): Single<RegistrationAgreementStatus> {
//        return api.checkRegistrationAgreement(eventId.toInt())
//    }
//
//    override fun acceptRegistrationAgreement(eventId: String): Single<RegistrationAgreementStatus> {
//        return api.acceptRegistrationAgreement(eventId.toInt())
//    }
//
//    override fun acceptEventAgreement(event: EventNew, withAccept: Boolean): Single<EventNew> {
//        return if (withAccept) acceptRegistrationAgreement(event.id.toString())
//            .doOnSuccess { if (it.isAccepted()) event.state?.agreement?.setAccepted() }
//            .map { event }
//        else Single.just(event)
//    }
//
//
//
//    override fun addOrRemoveEventFavorite(event: EventNew): Single<Optional<EventUserFavorite>> {
//        return if (event.binds?.userFavorite != null)
//            deleteFromFavorites(event.binds?.userFavorite?.id.toString())
//                .andThen(Single.just(Optional(null)))
//        else addEventToFavorites(event.id.toString())
//            .map { Optional(EventUserFavorite(it.id, it.user)) }
//    }
//
//    override fun addOrRemoveSubEventCalendar(subEvent: EventActivityModel): Single<Optional<EventCalendarModel>> {
//        val userCalendar = subEvent.binds?.userCalendar
//        return if (userCalendar == null)
//            addEventToCalendarWithResult(
//                com.examle.data.bodies.EventCalendarBody(
//                    appData.getId(),
//                    com.examle.data.bodies.EventCalendarBodyEntity(
//                        com.examle.data.bodies.EventCalendarBody.CALENDAR_EVENT_ACTIVITY,
//                        subEvent.id ?: 0
//                    )
//                )
//            ).map { Optional(it) }
//        else deleteCalendarEvent(userCalendar.id.toString())
//            .andThen(Single.just(Optional(null)))
//    }

}