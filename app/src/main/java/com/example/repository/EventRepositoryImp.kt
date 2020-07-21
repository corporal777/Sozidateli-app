package com.example.repository

import com.example.api.Api
import com.example.data.AppData
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
import javax.inject.Inject

class EventRepositoryImp
@Inject constructor(
        private val api: Api,
        appData: AppData
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

    override fun eventRegisterCheck(eventId: String): Single<EventRegisterCheckFields> {
        return call(api.eventRegisterCheck(eventId))
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
                        findRegistrationDataValue(field, responseField).fromJson(EventFile.Deserializer())
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
}