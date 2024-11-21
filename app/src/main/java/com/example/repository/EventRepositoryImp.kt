package com.example.repository

import android.util.Log
import com.example.api.Api
import com.example.data.AppData
import com.example.data.bodies.AddToFavoriteEntityModel
import com.example.data.bodies.AddToFavoriteModel
import com.example.data.bodies.EventCalendarBody
import com.example.data.bodies.MessageToEventBody
import com.example.data.bodies.RegisterToEventBody
import com.example.data.models.AddFavoriteModel
import com.example.data.models.ApiNewResponse
import com.example.data.models.EventActivityModel
import com.example.data.models.EventCalendarModel
import com.example.data.models.EventFormModel
import com.example.data.models.EventFormResultDraftModel
import com.example.data.models.EventFormResultFieldsModel
import com.example.data.models.EventFormResultModel
import com.example.data.models.EventNew
import com.example.data.models.EventRegisterField
import com.example.data.models.EventRegisterProfilePrefilledData
import com.example.data.models.EventSubscriptionRequest
import com.example.data.models.EventTagModel
import com.example.data.models.EventUserFavorite
import com.example.data.models.MemberModel
import com.example.data.models.NewEventFormat
import com.example.data.models.PageModel
import com.example.data.models.PartnerModel
import com.example.data.models.RegistrationAgreementStatus
import com.example.data.models.UserProfileFieldsModel
import com.example.util.pagination.PaginationResponse
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import okhttp3.RequestBody
import javax.inject.Inject

class EventRepositoryImp
@Inject constructor(
    private val api: Api,
    private val appData: AppData
) : ApiRepository(appData), EventRepository {


    //Alfa API
    override fun getEventByCode(code: String): Single<EventNew> {
        return api.getEventsList(
            mapOf(
                EventNew.EVENT_LIMIT to 1,
                EventNew.EVENT_OFFSET to 0,
                EventNew.EVENT_CODE to code
            )
        ).flatMapSingle {
            if (it.data.isNullOrEmpty()) Single.error(NullPointerException())
            else Single.just(it.data.first())
        }
    }

    override fun getEvent(eventId: String, binds: String?): Maybe<EventNew> {
        return api.getEventDetails(eventId, binds ?: "")
    }

    override fun getEventDetails(eventId: String): Maybe<EventNew> =
        getEventFormatsList(mapOf(EventNew.EVENT_LIMIT to 100, EventNew.EVENT_OFFSET to 0))
            .flatMap {
                api.getEventDetails(
                    eventId,
                    "organization,organization.userFavorite," +
                            "tag,page,format,activity,activity.userCalendar,activity.auditorium," +
                            "partner,member,member.user,userFavorite,destination-scheme," +
                            "user-registration,eventRegistrationState,current-user-registration," +
                            "current-user-registration-state," +
                            "is-user-subscribed,event-subscribe," +
                            "user-form-result"
                ).map {
                    val formats = appData.getEventFormats()
                    if (!formats.isNullOrEmpty()) {
                        it.format?.name =
                            formats.firstOrNull { f -> f.id == it.format?.value }?.name
                    }
                    it
                }
            }


    override fun getEventDetailForRegister(eventId: String): Maybe<EventNew> =
        api.getEventDetails(eventId, "user-form-result")


    override fun getEventsList(map: Map<String, Any>): Maybe<PaginationResponse<EventNew?>> =
        api.getEventsList(map)
            .map { PaginationResponse(it.totalCount, it.data ?: arrayListOf()) }

    override fun getEventsListNew(map: Map<String, Any>): Maybe<PaginationResponse<EventNew>> {
        return api.getEventsListNew(map)
            .map { PaginationResponse(it.totalCount, it.data ?: emptyList()) }
    }

    override fun getSortedEventsList(map: Map<String, Any>): Maybe<PaginationResponse<EventNew>> =
        api.getSortedEventsList(map)
            .map { PaginationResponse(it.totalCount, it.data ?: arrayListOf()) }

    override fun getOrganizationEventsList(map: Map<String, Any>): Maybe<PaginationResponse<EventNew?>> {
        return api.getOrganizationEventsList(map)
            .map { PaginationResponse(it.totalCount, it.data ?: arrayListOf()) }
    }


    override fun getEventFormatsList(map: Map<String, Any>): Maybe<List<NewEventFormat>> {
        val eventFormats = appData.getEventFormats()
        return if (eventFormats.isNullOrEmpty())
            api.getEventFormatsList(map)
                .doOnSuccess { appData.setEventFormats(it.data) }.map { it.data }
        else Maybe.just(eventFormats)
    }

    override fun getActiveEventFormatsList(): Maybe<List<NewEventFormat>> {
        return api.getActiveEventFormatsList().map { it.data }
    }


    override fun getEventMember(memberId: String): Maybe<MemberModel> =
        api.getEventMember(
            memberId,
            "user,user.userFavorite,user.chat-room-with-me,activities,activity.userCalendar"
        )

    override fun createEventSubscription(eventId: Int): Completable =
        api.createEventSubscription(EventSubscriptionRequest(eventId))

    override fun deleteEventSubscription(eventId: Int): Completable =
        api.deleteEventSubscription(eventId)

    override fun mailToEvent(body: MessageToEventBody): Completable =
        api.messageToEvent(body)

    override fun getPageDetails(pageId: String): Single<PageModel> =
        api.getPageDetails(pageId)

    override fun getSpeakers(map: Map<String, Any>): Maybe<PaginationResponse<MemberModel>> =
        api.getSpeakers(map)
            .map {
                PaginationResponse(
                    it.totalCount,
                    it.data
                )
            }

    override fun getSpeakersWithoutPagination(map: Map<String, Any>): Maybe<List<MemberModel>> =
        api.getSpeakers(map)
            .map {
                it.data
            }

    override fun getUserCalendarEvents(): Maybe<List<EventNew>?> =
        api.getUserCalendarEvents("activity.userCalendar,activity.auditorium,activity.member")
            .map {
                it.data
            }

    override fun getPartnerDetails(partnerId: String): Single<PartnerModel> =
        api.getPartnerDetails(partnerId, "event")

    override fun addToFavorites(body: AddToFavoriteModel): Single<AddFavoriteModel> =
        api.addToFavorite(body)

    override fun deleteFromFavorites(id: String): Completable {
        return if (appData.isTemporaryUser()) api.deleteFromTempFavorite(id)
        else api.deleteFromFavorite(id)
    }

    override fun addEventToFavorites(eventId: String): Single<AddFavoriteModel> {
        return if (appData.isTemporaryUser()) api.addToTempFavorite(
            AddToFavoriteModel(
                appData.getTempId(),
                AddToFavoriteEntityModel(
                    AddToFavoriteEntityModel.FAVORITE_EVENT,
                    eventId.toInt()
                )
            )
        ).map { AddFavoriteModel(it.id, it.tempUser) }
        else api.addToFavorite(
            AddToFavoriteModel(
                appData.getId(),
                AddToFavoriteEntityModel(
                    AddToFavoriteEntityModel.FAVORITE_EVENT,
                    eventId.toInt()
                )
            )
        )
    }



    override fun addOrgToFavorites(orgId: String): Single<AddFavoriteModel> {
        return if (appData.isTemporaryUser()) api.addToTempFavorite(
            AddToFavoriteModel(
                appData.getTempId(),
                AddToFavoriteEntityModel(
                    AddToFavoriteEntityModel.FAVORITE_ORGANIZATION,
                    orgId.toInt()
                )
            )
        ).map { AddFavoriteModel(it.id, it.tempUser) }
        else api.addToFavorite(
            AddToFavoriteModel(
                appData.getId(),
                AddToFavoriteEntityModel(
                    AddToFavoriteEntityModel.FAVORITE_ORGANIZATION,
                    orgId.toInt()
                )
            )
        )
    }

    override fun addUserToFavorites(speakerId: String): Single<AddFavoriteModel> {
        return if (appData.isTemporaryUser()) api.addToTempFavorite(
            AddToFavoriteModel(
                appData.getTempId(),
                AddToFavoriteEntityModel(
                    AddToFavoriteEntityModel.FAVORITE_SPEAKER,
                    speakerId.toInt()
                )
            )
        ).map { AddFavoriteModel(it.id, it.tempUser) }
        else api.addToFavorite(
            AddToFavoriteModel(
                appData.getId(),
                AddToFavoriteEntityModel(
                    AddToFavoriteEntityModel.FAVORITE_SPEAKER,
                    speakerId.toInt()
                )
            )
        )
    }


    override fun checkUserProfile(): Maybe<UserProfileFieldsModel> =
        api.checkUserProfile(appData.getId().toString())

    override fun getEventFavoritesList(map: Map<String, Any>): Maybe<PaginationResponse<EventNew?>> {
        return api.getEventFavoritesList(map)
            .map {
                it.data.forEach { org ->
                    org.entity?.model?.binds?.userFavorite =
                        EventUserFavorite(org.id?.toLong(), org.user)
                }
                PaginationResponse(it.totalCount, it.data.map { org -> org.entity?.model })
            }
    }

    override fun getEventForm(map: Map<String, Any>): Single<ApiNewResponse<List<EventFormModel/*EventRegisterField*/>>> =
        api.getEventForm(map)/*.map {
                val result = mutableListOf<EventRegisterField>()
                it.data[0].fields?.forEach { field ->
                    result.add(EventRegisterField(field.id.toString(), field.name, field.sort?: 0,
                    field.type?: EventRegisterField.Type.STRING, field.isRequired,
                    field.description, null, null, null, null))
                }
                ApiNewResponse(result.toList() , it.totalCount)
            }*/

    override fun getEventFormResult(map: Map<String, Any>): Single<ApiNewResponse<List<EventFormResultModel>>> =
        api.getEventFormResult(map)

    //+
    override fun getEventFormResultDraft(
        id: Int,
        map: Map<String, Any>
    ): Maybe<EventFormResultDraftModel> {
        return api.getEventFormResultDraft(id, map)
    }

    override fun saveEventFormResultDraft(body: RequestBody): Single<EventFormResultModel> =
        api.sendEventFormResultForRegister(body)

    override fun eventRegisterNew(body: RequestBody): Single<ApiNewResponse<List<EventFormResultModel>>> =
        api.eventRegister(body)

    //+
    override fun getPrefilledEventFormResult(id: String): Single<EventRegisterProfilePrefilledData> {
        return api.getPrefilledEventFormResult(id)
    }

    override fun registerToEvent(eventId: Int): Completable =
        api.registerToEvent(eventId, RegisterToEventBody(appData.getId()))

    override fun cancelRegisterToEvent(eventId: Int): Completable =
        api.cancelRegisterToEvent(eventId)

    override fun addEventToCalendar(body: EventCalendarBody): Completable =
        api.addEventToCalendar(body).doOnComplete {
            appData.defaultEvent = body.entity.id
        }

    override fun addEventToCalendarWithResult(body: EventCalendarBody): Single<EventCalendarModel> =
        api.addEventToCalendarWithResult(body).doOnSuccess {
            appData.defaultEvent = body.entity.id
        }

    override fun deleteAllCalendarEvents(entityType: String): Completable =
        api.deleteAllCalendarEvents(appData.getId(), entityType).doOnComplete {
            appData.defaultEvent = null
        }


    override fun deleteCalendarEvent(id: String): Completable =
        api.deleteCalendarEvent(id)

    override fun getUserCalendarEvent(entityType: String): Maybe<ApiNewResponse<List<EventCalendarModel>>> =
        api.getUserCalendarEvent(appData.getId(), entityType)

    override fun getEventActivities(eventId: Int): Maybe<List<EventActivityModel>> =
        api.getEventActivities(
            eventId,
            "event,member,tag,auditorium,userCalendar,member.user,member.user.userFavorite,userFavorite",
            "holdingDate.from",
            "asc"
        ).map { it.data }

    override fun getEventActivityDetail(activityId: String): Single<EventActivityModel> =
        api.getEventActivity(
            activityId,
            "event,member,auditorium,tag,userCalendar,member.user.userFavorite,member.user,userFavorite"
        )

    override fun getTags(map: Map<String, Any>): Maybe<List<EventTagModel>> =
        api.getTags(map).map { it.data }

    override fun searchEvents(map: Map<String, Any>): Maybe<PaginationResponse<EventNew>> {
        return api.searchGlobal(map)
            .map { PaginationResponse(it.events.count, it.events.data) }
    }


    override fun checkRegistrationAgreement(eventId: String): Single<RegistrationAgreementStatus> {
        return api.checkRegistrationAgreement(eventId.toInt())
    }

    override fun acceptRegistrationAgreement(eventId: String): Single<RegistrationAgreementStatus> {
        return api.acceptRegistrationAgreement(eventId.toInt())
    }
}