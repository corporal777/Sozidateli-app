package com.example.ui.home

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.data.models.EventNew
import com.example.data.models.EventNew.Companion.EVENT_BINDS
import com.example.data.models.EventNew.Companion.EVENT_LIMIT
import com.example.data.models.EventNew.Companion.EVENT_OFFSET
import com.example.data.models.EventNew.Companion.EVENT_PUBLIC
import com.example.data.models.EventNew.Companion.EVENT_SORT_FIELD
import com.example.data.models.EventNew.Companion.EVENT_SORT_TYPE
import com.example.data.models.EventNew.Companion.EVENT_STATUS
import com.example.extensions.build
import com.example.extensions.buildFlow
import com.example.repository.EventRepository
import com.example.ui.base.BaseViewModel
import com.example.util.pagination.PaginationResponse
import com.example.util.paginationNew.PagingDataSourceFactory
import com.example.util.paginationNew.PagingSourceFactory
import com.example.util.paginationNew.applyErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Maybe
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
@Inject constructor(
    private val eventRepository: EventRepository
) : BaseViewModel() {

    private val pagination = PagingSourceFactory { limit, offset ->
        getPaginationRequest(limit, offset)
    }.build(initialSize = 20, distance = 2)

    val events = pagination
        .catch { it.printStackTrace() }
        .cachedIn(viewModelScope)


    private fun getPaginationRequest(
        limit: Int,
        offset: Int
    ): Deferred<PaginationResponse<EventNew>> {
        return eventRepository.getEventsListNew(
            mapOf(
                EVENT_LIMIT to limit,
                EVENT_OFFSET to offset,
                EVENT_SORT_TYPE to "desc",
                EVENT_SORT_FIELD to "id",
                EVENT_BINDS to "current-user-registration,current-user-registration-state,eventRegistrationState",
                EVENT_PUBLIC to "true",
                EVENT_STATUS to "approved,registration,registrationFinished,running"
            )
        )
    }
}