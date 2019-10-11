package com.example.ui.organizations.events

import com.arellomobile.mvp.InjectViewState
import com.example.repository.EventRepository
import com.example.ui.event.list.EventListPresenter
import com.example.util.pagination.PaginationDataSourceFactory
import javax.inject.Inject

@InjectViewState
class OrganizationEventsPresenter
@Inject constructor(
        private val eventRepository: EventRepository
) : EventListPresenter<OrganizationEventsContract.View>(), OrganizationEventsContract.Presenter {

    lateinit var organizationId: String

    override val pagination = PaginationDataSourceFactory { limit, offset ->
        eventRepository.getEventList(limit, offset, mapOf("organization" to organizationId))
    }
}