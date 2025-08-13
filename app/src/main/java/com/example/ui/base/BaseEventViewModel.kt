package com.example.ui.base

import com.examle.data.AppData
import com.examle.domain.interactor.EventInteractor
import com.examle.domain.model.PaginationResponse
import com.examle.domain.model.event.EventModel
import com.example.common.constants.EVENT_BINDS
import com.example.common.constants.EVENT_LIMIT
import com.example.common.constants.EVENT_OFFSET
import com.example.common.constants.EVENT_PUBLIC
import com.example.common.constants.EVENT_SORT_FIELD
import com.example.common.constants.EVENT_SORT_TYPE
import com.example.common.constants.EVENT_STATUS
import com.example.extensions.build
import com.example.util.paginationNew.PagingSourceFactory
import kotlinx.coroutines.flow.MutableStateFlow

abstract class BaseEventViewModel(
    private val appData: AppData
) : BaseViewModel(appData) {


    val pagination = PagingSourceFactory { limit, offset ->
        paginationRequest(limit, offset)
    }.build(initialSize = 20, distance = 2)


    abstract suspend fun paginationRequest(limit: Int, offset: Int): PaginationResponse<EventModel>
}