package com.example.ui.organizations

import com.arellomobile.mvp.InjectViewState
import com.example.data.models.Event
import com.example.data.models.Organization
import com.example.data.models.OrganizationData
import com.example.repository.EventRepository
import com.example.repository.OrganizationRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.PaginationResponse
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class OrganizationPresenter
@Inject constructor(
        private val organizationRepository: OrganizationRepository,
        private val eventRepository: EventRepository
) : BasePresenter<OrganizationContract.View>(), OrganizationContract.Presenter {

    lateinit var organizationId: String
    private var scroll = 0

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        val organization = organizationRepository.getOrganizationById(organizationId)
        val events = eventRepository.getEventList(LISTS_LIMIT, 0, organisation = listOf(organizationId))
        compositeDisposable += Single.zip(
                organization,
                events.toSingle(PaginationResponse(0, emptyList())),
                BiFunction<Organization, PaginationResponse<Event>, OrganizationData> { t1, t2 ->
                    OrganizationData(t1, t2.data, t2.totalCount ?: 0)
                }
        )
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    viewState.setOrganization(it.organization, it.events, it.totalEvents, emptyList(), 0, LISTS_LIMIT)
                }, {
                    it.printStackTrace()
                })
    }

    override fun attachView(view: OrganizationContract.View?) {
        super.attachView(view)
        viewState.changeScrollY(scroll)
    }

    override fun onShowMoreEventsClick() {
        viewState.showEvents(organizationId)
    }

    override fun onEventClick(event: Event) {
        viewState.showAboutEvent(event)
    }

    override fun onGoToEventClick(event: Event) {
        viewState.showEventRequest(event)
    }

    override fun onScrollPositionChange(scroll: Int) {
        this.scroll = scroll
    }

    companion object {
        private const val LISTS_LIMIT = 3
    }
}
