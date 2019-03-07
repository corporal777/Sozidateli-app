package com.example.repository

import com.example.api.Api
import com.example.data.AppData
import com.example.data.models.Document
import com.example.data.models.Event
import com.example.data.models.News
import com.example.data.models.Notification
import com.example.data.models.user.User
import com.example.util.pagination.PaginationResponse
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import javax.inject.Inject

class EventRepositoryImp
@Inject constructor(
        private val api: Api,
        private val appData: AppData
) : ApiRepository(appData), EventRepository {

    override fun getEventList(limit: Int, offset: Int): Maybe<PaginationResponse<Event>> {
        return callPagination(api.getEventList(null,null,null,limit,offset))
    }

    override fun getEventNewsList(eventId: Int, limit: Int, offset: Int): Maybe<PaginationResponse<News>> {
        return callPagination(api.getEventNewsList(eventId,limit,offset))
    }

    override fun getNewsById(eventId: Int, newsId: Int): Single<News> {
        return call(api.getNewsById(eventId,newsId))
    }

    override fun getEventDocuments(eventId: Int, limit: Int, offset: Int): Maybe<PaginationResponse<Document>> {
        return callPagination(api.getEventDocs(eventId,limit,offset))
    }
}