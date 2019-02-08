package com.example.repository

import com.example.api.Api
import com.example.data.AppData
import com.example.data.models.Event
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
}