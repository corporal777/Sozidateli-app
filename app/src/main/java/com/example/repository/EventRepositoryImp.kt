package com.example.repository

import com.example.api.Api
import com.example.data.AppData
import com.example.data.models.*
import com.example.data.models.user.User
import com.example.util.pagination.PaginationResponse
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
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

    override fun getEventRegisterField(eventId: String): Single<RegisterFieldsData> {
        return call(api.getEventRegisterField(eventId))
    }

    override fun eventRegister(eventId: String, fields: Map<String, RequestBody?>, files: List<MultipartBody.Part?>?): Completable {
        return call(api.eventRegister(eventId, fields.let { if (it.isNullOrEmpty()) hashMapOf("_" to "_".toRequestBody("text/plain".toMediaTypeOrNull())) else it }, files.let { if (it.isNullOrEmpty()) null else it }))
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

    override fun getCategoriesList(): Single<List<Category>> {
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
}