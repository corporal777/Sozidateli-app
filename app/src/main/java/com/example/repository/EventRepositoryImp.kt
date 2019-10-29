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

    override fun getEventList(limit: Int, offset: Int, filter: Map<String, Any>?): Maybe<PaginationResponse<Event>> {
        return callPagination(api.getEventList(limit, offset, filter))
    }

    override fun getEventRecommendations(limit: Int, offset: Int): Maybe<PaginationResponse<Event>> {
        return callPagination(api.getEventRecommendations(limit, offset))
    }

    override fun getNewsById(eventId: Int, newsId: Int): Single<News> {
        return call(api.getNewsById(eventId, newsId))
    }

    override fun getEventRegisterField(eventId: String): Single<EventRegisterForm> {
        return call(api.getEventRegisterField(eventId))
    }

    override fun eventRegister(eventId: String, body: RequestBody): Single<EventRegisterResponse> {
        return call(api.eventRegister(eventId, body))
    }

    override fun getEventRegister(eventId: String): Single<EventRegisterResponse> {
        return call(api.getEventRegister(eventId))
    }

    override fun getEventActivity(eventId: Int): Maybe<List<SubEvent>> {
        return call(api.getEventActivity(eventId))
    }

    override fun getEventInfo(eventId: String): Maybe<EventInfo> {
        return call(api.getEventInfo(eventId))
    }

    override fun setDefaultEvent(eventId: String): Completable {
        return call(api.setDefaultEvent(eventId))
    }

    override fun addEventToCalendar(eventId: String, subEventId: Int): Completable {
        return call(api.addSubEventToCalendar(eventId, subEventId))
    }

    override fun removeEventFromCalendar(eventId: String, subEventId: Int): Completable {
        return call(api.removeSubEventFromCalendar(eventId, subEventId))
    }

    override fun getPartnerListByEvent(eventId: String): Single<List<Partner>> {
        return call(api.getPartnerListByEvent(eventId))
    }

    override fun getPartnerById(eventId: String, partnerId: String): Single<Partner> {
        return call(api.getPartnerById(eventId, partnerId))
    }

    override fun getSubevent(eventId: Int, subEventId: Int): Single<SubeventInfo> {
        return call(api.getSubEvent(eventId, subEventId))
    }

    override fun getSubeventUsers(eventId: Int, subEventId: Int, limit: Int, offset: Int): Maybe<PaginationResponse<User>> {
        return callPagination(api.getSubEventUsers(eventId, subEventId, limit, offset))
    }

    override fun getCategoriesList(): Single<List<EventGroup>> {
        return call(api.getCategoriesList())
    }

    override fun getEventSpeakers(eventId: String, limit: Int, offset: Int): Maybe<PaginationResponse<Speaker>> {
        return callPagination(api.getEventSpeakers(eventId, limit, offset))
    }

    override fun setEventRating(eventId: Int, value: Int): Completable {
        return call(api.setEventRating(eventId, value))
    }

    override fun getEventByCode(code: String): Single<Event> {
        return call(api.getEventByCode(code))
    }

    override fun getPage(event: String, page: String): Single<Page> {
        return call(api.getEventPage(event, page))
    }

    override fun loadEventRegistrationData(eventId: String): Single<EventRegisterData> {
        val loadFields = getEventRegisterField(eventId)
        val loadRegister = getEventRegister(eventId)

        return Single.zip(loadFields, loadRegister, BiFunction<EventRegisterForm, EventRegisterResponse, EventRegisterData> { fields, registration ->
            val findRegistrationDataValue: (EventRegisterField) -> JsonElement? = { field -> registration.fields?.find { field.id == it?.id }?.value }

            var group: EventRegisterField? = null
            val fieldsData = fields.fields?.mapNotNull { field ->
                when (field.type) {
                    EventRegisterField.Type.STRING,
                    EventRegisterField.Type.TEXT_AREA,
                    EventRegisterField.Type.NUMBER -> EventRegisterFieldData.String(field, findRegistrationDataValue(field).fromJson<String>())
                    EventRegisterField.Type.DATE,
                    EventRegisterField.Type.DATETIME -> EventRegisterFieldData.Date(field, findRegistrationDataValue(field).fromJson<String>())
                    EventRegisterField.Type.CHECKBOX -> EventRegisterFieldData.Checkbox(field, findRegistrationDataValue(field).fromJson<Set<String>>())
                    EventRegisterField.Type.SELECT_BOX -> EventRegisterFieldData.SelectBox(field, findRegistrationDataValue(field).fromJson<String>())
                    EventRegisterField.Type.RADIO_BOX -> EventRegisterFieldData.RadioBox(field, findRegistrationDataValue(field).fromJson<String>())
                    EventRegisterField.Type.FILE -> EventRegisterFieldData.File(field, findRegistrationDataValue(field).fromJson(EventFile.Deserializer()))
                    EventRegisterField.Type.BOOLEAN -> EventRegisterFieldData.Boolean(field, findRegistrationDataValue(field).fromJson<Boolean>())
                    EventRegisterField.Type.PASSPORT -> EventRegisterFieldData.Passport(field, findRegistrationDataValue(field).fromJson<EventPassport>())
                    EventRegisterField.Type.GROUP -> {
                        group = field
                        null
                    }
                }
            }

            EventRegisterData(
                    registration.event,
                    group,
                    registration.group_id,
                    fields.groups ?: emptyList(),
                    fieldsData ?: emptyList()
            )
        })
    }
}