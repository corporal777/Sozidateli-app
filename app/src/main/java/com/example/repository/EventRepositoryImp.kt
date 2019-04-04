package com.example.repository

import com.example.api.Api
import com.example.data.AppData
import com.example.data.models.*
import com.example.util.pagination.PaginationResponse
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class EventRepositoryImp
@Inject constructor(
        private val api: Api,
        appData: AppData
) : ApiRepository(appData), EventRepository {

    override fun getEventList(limit: Int, offset: Int): Maybe<PaginationResponse<Event>> {
        return callPagination(api.getEventList(null, null, null, limit, offset))
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
        return call(api.eventRegister(eventId, fields, files))
    }

    override fun getEventRegister(eventId: Int): Single<EventRegisterResponse> {
        return call(api.getEventRegister(eventId))
    }

    override fun getEventDaySchedule(eventId: Int, body: Map<String, Any>, limit: Int, offset: Int): Maybe<PaginationResponse<SubEvent>> {
        return callPagination(api.getEventDaySchedule(eventId, body, limit, offset))
    }

    override fun getEventInfo(eventId: Int): Maybe<EventInfo> {
        return call(api.getEventInfo(eventId))
    }

    override fun getEventRegisterList(limit: Int, offset: Int): Maybe<PaginationResponse<EventRegisterResponse>> {
        return callPagination(api.getEventRegisterList(limit, offset))
    }

    override fun setDefaultEvent(eventId: Int): Completable {
        return call(api.setDefaultEvent(eventId))
    }

    override fun addEventToCalendar(eventId: Int, subEventId: Int): Completable {
        return call(api.addEventToCalendar(eventId, subEventId))
    }

    override fun removeEventFromCalendar(eventId: Int, subEventId: Int): Completable {
        return call(api.removeEventFromCalendar(eventId, subEventId))
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
}