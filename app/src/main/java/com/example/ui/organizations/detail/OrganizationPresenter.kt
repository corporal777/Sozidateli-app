package com.example.ui.organizations.detail

import call
import com.example.data.AppData
import com.example.data.models.*
import com.example.data.socket.SocketIOManager
import com.example.repository.EventRepository
import com.example.repository.OrganizationRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.PaginationResponse
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import withProgressBarDialogLoading
import javax.inject.Inject


@InjectViewState
class OrganizationPresenter
@Inject constructor(
    private val appData: AppData,
    private val organizationRepository: OrganizationRepository,
    private val userRepository: UserRepository,
    private val eventRepository: EventRepository,
    private val socket: SocketIOManager
) : BasePresenter<OrganizationContract.View>(appData), OrganizationContract.Presenter {

    lateinit var organizationId: String
    private var isFirstAttach = true


    override fun attachView(view: OrganizationContract.View?) {
        super.attachView(view)
        loadData()
    }


    private fun loadData() {
        compositeDisposable += organizationDataRequest()
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = {
                    viewState.apply {
                        setMainData(it.organization)
                        setInformationData(it.organization)
                        setEventsData(it.events ?: emptyList())
                        setMembersData(it.member ?: emptyList(), it.membersSize)
                    }
                })
    }


    override fun onAddUserFavoriteCLick(member: OrganizationMemberModel) {
        compositeDisposable += Single.defer {
            if (member.binds?.userFavorite == null)
                eventRepository.addUserToFavorites(member.user.toString()).map { true }
            else eventRepository.deleteFromFavorites(member.binds.userFavorite?.id.toString())
                .andThen(Single.just(false))
        }
            .flatMapMaybe { e -> getMembersRequest().map { Triple(it.data, it.totalCount, e) } }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = {
                    viewState.apply {
                        setMembersData(it.first, it.second ?: it.first.size)
                        if (it.third) showAddedToFavoriteDialog()
                        else showRemovedFromFavoriteDialog()
                    }
                }
            )
    }

    override fun onAddOrganizationFavoriteClick(organization: OrganizationNew) {
        compositeDisposable += Single.defer {
            if (organization.binds?.userFavorite == null)
                eventRepository.addOrgToFavorites(organizationId).flatMap {
                    organization.binds?.userFavorite = EventUserFavorite(it.id, it.user)
                    Single.just(organization)
                }
            else eventRepository.deleteFromFavorites(organization.binds?.userFavorite?.id.toString())
                .andThen(Single.just(organization.apply { binds?.userFavorite = null }))
        }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = {
                    viewState.apply {
                        updateOrganizationSubscription(it)
                        if (it.binds?.userFavorite != null) showAddedToFavoriteDialog()
                        else showRemovedFromFavoriteDialog()
                    }
                }
            )
    }

    override fun onActionRegister(event: String, url: String?, formEnabled: Boolean) {
        if (url.isNullOrEmpty()) registerToEvent(event, formEnabled)
        else compositeDisposable += eventRepository.checkRegistrationAgreement(event)
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = {
                    if (it.isAccepted()) registerToEvent(event, formEnabled)
                    else viewState.showAgreementRegisterDialog(event, url, formEnabled)
                }
            )
    }

    override fun onAcceptRegistrationAgreement(event: String, formEnabled: Boolean) {
        compositeDisposable += eventRepository.acceptRegistrationAgreement(event)
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = { if (it.isAccepted()) registerToEvent(event, formEnabled) }
            )
    }

    private fun registerToEvent(event: String, formEnabled: Boolean) {
        if (formEnabled) viewState.showEventRequest(event)
        else eventRepository.registerToEvent(event.toInt())
            .andThen(socket.connectToUpdates())
            .andThen(eventRepository.getEvent(event, "organization,user-registration,current-user-registration,eventRegistrationState,current-user-registration-state"))
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = {
                    viewState.apply {
                        updateEvent(it)
                        showEventRegistrationSuccessDialog()
                    }
                }
            ).call(compositeDisposable)
    }

    override fun onActionCancel(event: String, registrationId: String?) {
        compositeDisposable += eventRepository.cancelRegisterToEvent(registrationId?.toInt() ?: 0)
            .andThen(eventRepository.getEvent(event, "organization,user-registration,current-user-registration,eventRegistrationState,current-user-registration-state"))
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple {
                viewState.updateEvent(it)
            }
    }


    override fun onUserClick(user: String?) {
        if (isCurrentUser(user.toString())) viewState.showCurrentUser()
        else viewState.showUser(user.toString())
    }

    override fun onShowAuthorization(event: String) {
        appData.savedEventId = event
        viewState.showAuthorization()
    }

    override fun onShowMoreEventsClick() = viewState.showAllEvents(organizationId)
    override fun onShowMoreUsersClick() = viewState.showAllUsers(organizationId)
    override fun onShowEventClick(event: String) = viewState.showAboutEvent(event)
    override fun onRefreshRequest() = loadData()

    private fun organizationDataRequest(): Maybe<AboutOrganizationData> {
        return Maybe.zip(
            organizationRepository.getOrganizationDetails(organizationId).toMaybe(),
            eventRepository.getOrganizationEventsList(
                mapOf(
                    EventNew.EVENT_ACTIVE to true,
                    EventNew.EVENT_LIMIT to 3,
                    EventNew.EVENT_OFFSET to 0,
                    EventNew.EVENT_BINDS to "organization,user-registration,current-user-registration,eventRegistrationState,current-user-registration-state",
                    EventNew.EVENT_ORGANIZATION to organizationId,
                    EventNew.EVENT_SORT_FIELD to "id"
                )
            ),
            getMembersRequest()
        ) { org, e, m ->
            AboutOrganizationData(
                org,
                e.data.mapNotNull { it },
                m.data,
                m.totalCount ?: m.data.size
            )
        }
    }

    fun isCurrentUser(id: String): Boolean {
        return appData.isCurrentUser(id)
    }

    private fun getMembersRequest(): Maybe<PaginationResponse<OrganizationMemberModel>> {
        return organizationRepository.getOrganizationMembers(
            mapOf(
                OrganizationMember.MEMBERS_BINDS to "user,userFavorite",
                OrganizationMember.MEMBERS_ORGANIZATION to organizationId
            )
        ).map {
            PaginationResponse(
                totalCount = it.totalCount,
                data = if (it.data.size > 3) it.data.subList(0, 3) else it.data
            )
        }
    }
}