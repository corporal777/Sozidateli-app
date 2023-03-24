package com.example.ui.organizations.events

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.UserEventData
import com.example.data.models.EventNew
import com.example.data.models.UserProfileFields
import com.example.di.Connectivity
import com.example.extensions.buildList
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.ui.event.list.EventListPresenter
import com.example.util.pagination.PaginationResponse
import com.example.util.pagination.observable.PaginationDataSourceFactory
import com.example.util.pagination.observable.PaginationList
import com.example.util.pagination.observable.applyErrorHandler
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCustomProgressBarLoadingDialog
import java.net.UnknownHostException
import javax.inject.Inject

@InjectViewState
class OrganizationEventsPresenter
@Inject constructor(
    val appData: AppData,
    private val eventRepository: EventRepository,
) : BasePresenter<OrganizationEventsContract.View>(appData,), OrganizationEventsContract.Presenter {

    lateinit var organizationId: String

    private val pagination: PaginationDataSourceFactory<EventNew?> = PaginationDataSourceFactory(::getPaginationRequest)
    private lateinit var paginationList: PaginationList<EventNew?>

    private var isFirstAttach = true

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setData(List(10) { null })
        paginationList = pagination.applyErrorHandler {
            if (it.cause is UnknownHostException) hasNoConnectionError = true
        }.buildList(enablePlaceholders = false)

        compositeDisposable += Observable.create(paginationList)
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                if (it.isEmpty()) viewState.showEmptyListPlaceholder()
                else viewState.setData(it)
            }
    }

    override fun attachView(view: OrganizationEventsContract.View?) {
        super.attachView(view)
        if (isFirstAttach) isFirstAttach = false
        else pagination.invalidate()
    }

    private fun checkRegistrationFields(event: String, fields: List<UserProfileFields>) {
        val filtered = fields.filter { it.filled == false }.mapNotNull { it.name }
        //val filtered = fields.mapNotNull { it.title }
        if (filtered.isEmpty()) {
            viewState.showEventRequest(event)
        } else {
            viewState.showRegistrationFieldsRequest(filtered)
        }
    }

    override fun onShowEditProfileClick() {
        viewState.showEditProfile(appData.getUser().user_id.toString())
    }

    override fun onActionCancel(event: String, registrationId: String?) {
        compositeDisposable += eventRepository.cancelRegisterToEvent(registrationId?.toInt()?: 0)
            .andThen(eventRepository.getEventDetails(event))
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple {
                paginationList.invalidate()
            }
    }


    override fun onShowEventClick(event: String) = viewState.showAboutEvent(event)
    override fun onActionRegister(event: String) = viewState.showEventRequest(event)
    override fun onItemTake(position: Int) = paginationList.onItemTake(position)
    override fun onRefreshRequest() = paginationList.invalidate()

    private fun getPaginationRequest(limit: Int, offset: Int): Maybe<PaginationResponse<EventNew?>> {
        return eventRepository.getOrganizationEventsList(
            mapOf(
                EventNew.EVENT_LIMIT to limit,
                EventNew.EVENT_OFFSET to offset,
                EventNew.EVENT_BINDS to "userFavorite,user-registration,current-user-registration,current-user-registration-state,eventRegistrationState,format",
                // EventNew.EVENT_SORT_TYPE to "desc",
                EventNew.EVENT_ORGANIZATION to organizationId
            )
        )
    }

}