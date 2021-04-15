package com.example.repository

import android.net.Uri
import com.example.api.Api
import com.example.api.NewApi
import com.example.data.AppData
import com.example.data.bodies.AddToFavoriteModel
import com.example.data.models.*
import com.example.data.models.user.User
import com.example.util.pagination.PaginationResponse
import com.google.gson.JsonElement
import fromJson
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import okhttp3.RequestBody
import retrofit2.http.Field
import javax.inject.Inject

class EventRepositoryImp
@Inject constructor(
        private val api: Api,
        private val newApi: NewApi,
        val appData: AppData
) : ApiRepository(appData), EventRepository {

    override fun getEventList(limit: Int, offset: Int, filter: Map<String, Any>?): Maybe<PaginationResponse<Event?>> {
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

    override fun loadEventRatingData(eventId: String): Single<EventRatingData> {
        return Single.just(EventRatingData(EventData("","", null,null,null,null,
                "",null,null,null,null,null,
                null,null,null,null,
                null,null,null,null,null,null,null
                ,null,null,null, arrayListOf(), arrayListOf()
                , arrayListOf(), arrayListOf(),null,null,null
                ,null,null,null,null,null
                ,false,false,null), arrayListOf(), null, null))
        /*return Single.zip(
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
        )*/
    }

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
                        null/*findRegistrationDataValue(field, responseField).fromJson(EventFile.Deserializer())*/
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

    override fun addToFavorite(eventId: String): Completable {
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
    }

    //Alfa API
    override fun getEventsList(map: Map<String, Any>): Maybe<PaginationResponse<EventNew?>> =
            newApi.getEventsList(map)
                    .map {
                        val eventFormats = appData.getEventFormats()
                        if (!eventFormats.isNullOrEmpty()) {
                            it.data?.forEach { ev ->
                                ev?.format?.name = eventFormats.firstOrNull { f -> f.id == ev?.format?.value }?.name
                            }
                        }
                        PaginationResponse(it.totalCount, it.data?: arrayListOf())
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

    /*override fun getEventDetails(eventId: String): Maybe<EventInfo> =
        newApi.getEventDetails(eventId, "rights,organization,tag,page,activity,user-registration,user-form-result,form,partner,member,userFavorite,auditorium")
                .map {
                    val eventFormats = appData.getEventFormats()
                    if (!eventFormats.isNullOrEmpty()) {
                        it.format?.name = eventFormats.firstOrNull { f -> f.id == it.format?.value }?.name
                    }
                    EventInfo(it, it.binds?.partner?: arrayListOf(), it.binds?.page?: arrayListOf(),
                            if (it.binds?.userRegister?.isNotEmpty() == true) it.binds.userRegister[0] else null, it.state?.rating?.askDelay, it.binds?.form)
                }*/

    override fun mailToEvent(message: String, event: String, isPush: Boolean, isInApp: Boolean): Completable =
            newApi.mailToEvent(message, event, isPush, isInApp)

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

    override fun getPartnerDetails(partnerId: String): Single<PartnerModel> =
            newApi.getPartnerDetails(partnerId, "event")

    override fun addToFavorites(body: AddToFavoriteModel): Single<AddFavoriteModel> =
            newApi.addToFavorite(body)

    override fun deleteFromFavorite(id: String): Completable =
            newApi.deleteFromFavorite(id)

    override fun checkUserProfile(): Single<List<UserProfileFields>> =
            newApi.checkUserProfile(appData.getId().toString())
}