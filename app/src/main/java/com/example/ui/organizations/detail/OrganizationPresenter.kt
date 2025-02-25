package com.example.ui.organizations.detail

import call
import com.example.data.AppData
import com.example.data.models.*
import com.example.data.socket.SocketIOManager
import com.example.exceptions.EventAgreementException
import com.example.repository.EventRepository
import com.example.repository.OrganizationRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.PaginationResponse
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import withCustomLoading
import withDelay
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
    private var onRequest: () -> Unit = {}

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        onRequest = {
            compositeDisposable += organizationDataRequest()
                .performOnBackgroundOutOnMain()
                .subscribeSimple(
                    onError = { onReceiveError(it) },
                    onSuccess = {
                        viewState.apply {
                            setOrganizationsData(it.organization)
                            setEventsData(it.events ?: emptyList(), it.eventsSize ?: 0)
                            setMembersData(it.member ?: emptyList(), it.membersSize ?: 0)
                        }
                    })
        }
        onRequest.invoke()
    }

    override fun onAddUserFavoriteCLick(member: OrganizationMemberModel) {
        compositeDisposable += Single.defer {
            if (member.binds?.userFavorite == null)
                eventRepository.addUserToFavorites(member.user.toString())
                    .map { Optional(EventUserFavorite(it.id, it.user)) }
            else eventRepository.deleteFromFavorites(member.binds.userFavorite?.id.toString())
                .andThen(Single.just(Optional(null)))
        }
            .doOnSuccess { member.binds?.userFavorite = it.value }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = {
                    viewState.apply {
                        updateUser(member)
                        if (it.value != null) showAddedToFavoriteDialog()
                        else showRemovedFromFavoriteDialog()
                    }
                }
            )
    }

    override fun onAddOrganizationFavoriteClick(organization: OrganizationNew) {
        compositeDisposable += organizationRepository.addOrRemoveOrgFavorite(organization)
            .doOnSuccess { organization.binds?.userFavorite = it.value }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = {
                    viewState.apply {
                        updateOrganization(organization)
                        if (it.value != null) showAddedToFavoriteDialog()
                        else showRemovedFromFavoriteDialog()
                    }
                }
            )
    }

    override fun onActionRegister(event: EventNew, withAccept: Boolean) {
        val url = event.userAgreement?.uri
        compositeDisposable += Completable.defer {
            if (withAccept) eventRepository.acceptRegistrationAgreement(event.id.toString())
                .doOnSuccess { if (it.isAccepted()) event.state?.agreement?.setAccepted() }
                .ignoreElement()
            else {
                if (url.isNullOrEmpty()) Completable.complete()
                else if (event.state?.isAgreementAccepted() == true) Completable.complete()
                else Completable.error(EventAgreementException()).withDelay(500)
            }
        }
            .andThen(registerToEvent(event))
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple(
                onError = {
                    if (it is EventAgreementException) viewState.showAgreementRegisterDialog(event)
                    else {
                        onReceiveError(it)
                        viewState.updateEvent(event)
                    }
                },
                onSuccess = {
                    if (it.isFormEnabled()) viewState.showEventRequest(event.id.toString())
                    else viewState.apply {
                        updateEvent(it)
                        showEventRegistrationSuccessDialog()
                    }
                }
            )
    }

    private fun registerToEvent(event: EventNew): Maybe<EventNew> {
        return if (event.isFormEnabled()) Maybe.just(event).withDelay(500)
        else eventRepository.registerToEvent(event.id ?: 0)
            .andThen(socket.connectToUpdates())
            .andThen(eventRepository.getEvent(event.id.toString()))
            .doOnSuccess { event.setFieldsForActionButton(it) }.map { event }
    }


    override fun onActionCancel(event: EventNew) {
        val registrationId = event.binds?.currentUserRegistration?.id.toString()
        compositeDisposable += eventRepository.cancelRegisterToEvent(registrationId.toInt())
            .andThen(eventRepository.getEvent(event.id.toString()))
            .doOnSuccess { event.setFieldsForActionButton(it) }.map { event }
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = { viewState.updateEvent(it) }
            )
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
    override fun onRefreshRequest() = onRequest.invoke()

    private fun organizationDataRequest(): Maybe<AboutOrganizationData> {
        return Maybe.zip(
            organizationRepository.getOrganizationDetails(organizationId).toMaybe(),
            eventRepository.getOrganizationEventsList(
                mapOf(
                    EventNew.EVENT_ACTIVE to true,
                    EventNew.EVENT_LIMIT to 3,
                    EventNew.EVENT_OFFSET to 0,
                    EventNew.EVENT_BINDS to "organization,current-user-registration,current-user-registration-state",
                    EventNew.EVENT_ORGANIZATION to organizationId,
                    EventNew.EVENT_SORT_FIELD to "id"
                )
            ),
            organizationRepository.getOrganizationMembers(
                mapOf(
                    OrganizationMember.MEMBERS_LIMIT to 3,
                    OrganizationMember.MEMBERS_OFFSET to 0,
                    OrganizationMember.MEMBERS_BINDS to "user,userFavorite",
                    OrganizationMember.MEMBERS_ORGANIZATION to organizationId
                )
            )
        ) { org, e, m -> AboutOrganizationData(org, e.data, e.totalCount, m.data, m.totalCount) }
    }

    fun isCurrentUser(id: String): Boolean {
        return appData.isCurrentUser(id)
    }
}