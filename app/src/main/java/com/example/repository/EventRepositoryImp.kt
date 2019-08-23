package com.example.repository

import com.example.api.Api
import com.example.data.AppData
import com.example.data.models.*
import com.example.data.models.user.User
import com.example.util.pagination.PaginationResponse
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import okhttp3.MediaType
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

    override fun getEventList(limit: Int, offset: Int, name: String?, dateStart: String?,
                              dateEnd: String?, category: List<String>?,
                              organisation: List<String>?, qr: String?): Maybe<PaginationResponse<EventApprove>> {
        return callPagination(api.getEventList(limit, offset, name, dateStart, dateEnd, category, organisation, qr))
                .map { response ->
                    PaginationResponse(
                            response.totalCount,
                            response.data.map { item -> EventApprove(item, EventApprove.Status.EMPTY) }
                    )
                }
    }

    override fun getEventNewsList(eventId: Int, limit: Int, offset: Int): Maybe<PaginationResponse<News>> {
        return callPagination(api.getEventNewsList(eventId, limit, offset))
    }

    override fun getNewsById(eventId: Int, newsId: Int): Single<News> {
        return call(api.getNewsById(eventId, newsId))
    }

    override fun getEventDocuments(eventId: Int, limit: Int, offset: Int): Maybe<PaginationResponse<Document>> {
        return callPagination(api.getEventDocs(eventId, limit, offset))
    }


    override fun getEventRegisterField(eventId: Int): Single<RegisterFieldResponse> {
        return call(api.getEventRegisterField(eventId))
    }

    override fun eventRegister(eventId: Int, fields: Map<String, RequestBody?>, files: List<MultipartBody.Part?>?): Completable {
        return call(api.eventRegister(eventId, fields.let { if (it.isNullOrEmpty()) hashMapOf("_" to "_".toRequestBody("text/plain".toMediaTypeOrNull())) else it }, files.let { if (it.isNullOrEmpty()) null else it }))
    }

    override fun getEventRegister(eventId: Int): Single<EventRegisterResponse> {
        return call(api.getEventRegister(eventId))
    }

    override fun getEventActivity(eventId: Int): Maybe<List<SubEvent>> {
        return call(api.getEventActivity(eventId))
    }

    override fun getEventInfo(eventId: Int): Maybe<EventInfo> {
        return call(api.getEventInfo(eventId))
    }

    override fun getEventRegisterList(limit: Int, offset: Int): Maybe<PaginationResponse<EventApprove>> {
        return callPagination(api.getEventRegisterList(limit, offset))
                .map { response ->
                    PaginationResponse(
                            response.totalCount,
                            response.data.mapNotNull { item ->
                                item.event?.let { EventApprove(it, item.status.toEventApproveStatus()) }
                            }
                    )
                }
    }

    override fun setDefaultEvent(eventId: Int): Completable {
        return call(api.setDefaultEvent(eventId))
    }

    override fun addEventToCalendar(eventId: Int, subEventId: Int): Completable {
        return call(api.addSubEventToCalendar(eventId, subEventId))
    }

    override fun removeEventFromCalendar(eventId: Int, subEventId: Int): Completable {
        return call(api.removeSubEventFromCalendar(eventId, subEventId))
    }

    override fun getEventMapInfo(eventId: Int): Single<MapInfo> {
        return call(api.getEventMapInfo(eventId))
    }

    override fun getPartnerListByEvent(eventId: Int): Single<List<Partner>> {
        return call(api.getPartnerListByEvent(eventId))
    }

    override fun getPartnerById(partnerId: Int): Single<Partner> {
        return call(api.getPartnerById(partnerId))
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

    override fun getEventSpeakers(eventId: Int, limit: Int, offset: Int): Maybe<PaginationResponse<Speaker>> {
        return callPagination(api.getEventSpeakers(eventId, limit, offset))
    }
}