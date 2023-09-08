package com.example.ui.organizations.detail

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.bodies.AddToFavoriteEntityModel
import com.example.data.bodies.AddToFavoriteEntityModel.Companion.FAVORITE_ORGANIZATION
import com.example.data.bodies.AddToFavoriteEntityModel.Companion.FAVORITE_SPEAKER
import com.example.data.bodies.AddToFavoriteModel
import com.example.data.models.*
import com.example.repository.EventRepository
import com.example.repository.OrganizationRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.PaginationResponse
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
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
) : BasePresenter<OrganizationContract.View>(appData), OrganizationContract.Presenter {

    lateinit var organizationId: String
    private var isFirstAttach = true

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadData()
    }

    override fun attachView(view: OrganizationContract.View?) {
        super.attachView(view)
        if (isFirstAttach) isFirstAttach = false
        else loadData()
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
        compositeDisposable += Single.create<Boolean> { emitter ->
            val disposables = CompositeDisposable()
            disposables += if (member.binds?.userFavorite == null) {
                eventRepository.addToFavorites(userFavoriteBody(member.user))
                    .subscribeSimple(
                        onError = { emitter.onError(it) },
                        onSuccess = { emitter.onSuccess(true) })
            } else {
                eventRepository.deleteFromFavorite(member.binds.userFavorite?.id.toString())
                    .subscribeSimple(
                        onError = { emitter.onError(it) },
                        onComplete = { emitter.onSuccess(false) })
            }
            emitter.setDisposable(disposables)
        }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = {
                    compositeDisposable += organizationMembersRequest()
                        .performOnBackgroundOutOnMain()
                        .subscribeSimple {
                            viewState.setMembersData(it.data, it.totalCount ?: it.data.size)
                        }
                    viewState.apply {
                        if (it) showEventAddedToFavoriteDialog()
                        else showEventRemovedFromFavoriteDialog()
                    }
                }
            )
    }

    override fun onAddOrganizationFavoriteClick(organization: OrganizationNew) {
        compositeDisposable += Single.create<OrganizationNew> { emitter ->
            val disposables = CompositeDisposable()
            disposables += if (organization.binds?.userFavorite == null) {
                eventRepository.addToFavorites(organizationFavoriteBody())
                    .subscribeSimple(
                        onError = { emitter.onError(it) },
                        onSuccess = {
                            organization.binds?.userFavorite = EventUserFavorite(it.id, it.user)
                            emitter.onSuccess(organization)
                        })
            } else {
                eventRepository.deleteFromFavorite(organization.binds?.userFavorite?.id.toString())
                    .subscribeSimple(
                        onError = { emitter.onError(it) },
                        onComplete = {
                            organization.binds?.userFavorite = null
                            emitter.onSuccess(organization)
                        })
            }
            emitter.setDisposable(disposables)
        }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = {
                    viewState.apply {
                        updateOrganizationSubscription(it)
                        if (it.binds?.userFavorite != null) showEventAddedToFavoriteDialog()
                        else showEventRemovedFromFavoriteDialog()
                    }
                }
            )
    }

    override fun onActionRegister(event: String, url: String?) {
        if (url.isNullOrEmpty()) viewState.showEventRequest(event)
        else {
            compositeDisposable += eventRepository.checkRegistrationAgreement(event)
                .performOnBackgroundOutOnMain()
                .withProgressBarDialogLoading(viewState)
                .subscribeSimple(
                    onError = { onReceiveError(it) },
                    onSuccess = {
                        if (it.isAccepted()) viewState.showEventRequest(event)
                        else viewState.showAgreementRegisterDialog(event, url)
                    }
                )
        }
    }

    override fun onActionCancel(event: String, registrationId: String?) {
        compositeDisposable += eventRepository.cancelRegisterToEvent(registrationId?.toInt() ?: 0)
            .andThen(eventRepository.getEventDetails(event))
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple {
                viewState.updateEvent(it.event)
            }
    }

    override fun onAcceptRegistrationAgreement(event: String) {
        compositeDisposable += eventRepository.acceptRegistrationAgreement(event)
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = { if (it.isAccepted()) viewState.showEventRequest(event) }
            )
    }

    override fun onUserClick(user: String?) {
        if (appData.isCurrentUser(user.toString())) {
            viewState.showCurrentUser(appData.getUserNew().id.toString())
        } else viewState.showUser(user.toString())
    }

    override fun onShowMoreEventsClick() = viewState.showAllEvents(organizationId)
    override fun onShowMoreUsersClick() = viewState.showAllUsers(organizationId)
    override fun onShowEventClick(event: String) = viewState.showAboutEvent(event)
    override fun onRefreshRequest() = loadData()

    private fun organizationDataRequest(): Maybe<AboutOrganizationData> {
        return Maybe.zip(
            organizationRepository.getOrganizationDetails(organizationId).toMaybe(),
            eventRepository.getOrganizationEventsListWithoutPagination(
                mapOf(
                    EventNew.EVENT_ACTIVE to true,
                    EventNew.EVENT_LIMIT to 3,
                    EventNew.EVENT_OFFSET to 0,
                    EventNew.EVENT_BINDS to "rights,organization,tag,page,activity,user-registration,user-form-result,userFavorite,current-user-registration,eventRegistrationState,current-user-registration-state",
                    EventNew.EVENT_ORGANIZATION to organizationId,
                    EventNew.EVENT_SORT_FIELD to "id"
                )
            ),
            organizationMembersRequest()
        ) { org, e, m ->
            AboutOrganizationData(org, e.data, m.data, m.totalCount ?: m.data.size)
        }
    }

    fun isCurrentUser(id: String): Boolean {
        return appData.isCurrentUser(id)
    }

    private fun organizationFavoriteBody(): AddToFavoriteModel {
        return AddToFavoriteModel(
            appData.getId(),
            AddToFavoriteEntityModel(FAVORITE_ORGANIZATION, organizationId.toInt())
        )
    }

    private fun organizationMembersRequest(): Maybe<PaginationResponse<OrganizationMemberModel>> {
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

    private fun userFavoriteBody(user: Int?): AddToFavoriteModel {
        return AddToFavoriteModel(
            appData.getId(),
            AddToFavoriteEntityModel(FAVORITE_SPEAKER, user)
        )
    }
}