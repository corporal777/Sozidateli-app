package com.example.ui.organizations.redesign

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.UserEventData
import com.example.data.bodies.AddToFavoriteEntityModel
import com.example.data.bodies.AddToFavoriteEntityModel.Companion.FAVORITE_ORGANIZATION
import com.example.data.bodies.AddToFavoriteEntityModel.Companion.FAVORITE_SPEAKER
import com.example.data.bodies.AddToFavoriteModel
import com.example.data.models.*
import com.example.repository.EventRepository
import com.example.repository.OrganizationRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.ui.organizations.redesign.data.AboutOrganizationData
import com.example.ui.views.UserSubscribeButton
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCustomProgressBarLoadingDialog
import javax.inject.Inject
import kotlin.math.abs


@InjectViewState
class OrganizationPresenterNew
@Inject constructor(
    private val appData: AppData,
    private val organizationRepository: OrganizationRepository,
    private val userRepository: UserRepository,
    private val eventRepository: EventRepository,
    private val eventData: UserEventData
) : BasePresenter<OrganizationContractNew.View>(appData), OrganizationContractNew.Presenter {

    lateinit var organizationId: String
    private var mDy = 0f


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadData(true)
        viewState.setAppBarElevation(mDy)
    }

    override fun attachView(view: OrganizationContractNew.View?) {
        super.attachView(view)
        viewState.setAppBarElevation(mDy)
    }

    override fun changeAppBarElevation(value: Int) {
        mDy = abs(value / 10f)
        viewState.setAppBarElevation(mDy)
    }

    private fun loadData(withLoading: Boolean) {
        compositeDisposable += organizationDataRequest()
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    onReceiveError(it)
                },
                onSuccess = {
                    viewState.apply {
                        setMainData(it.organization)
                        setInformationData(it.organization)
                        setEventsData(it.events ?: emptyList())
                        setMembersData(it.member ?: emptyList(), it.membersSize)
                    }
                })
    }


    override fun onUserActionCLick(userId: String) {
        compositeDisposable += userRepository.getUserByIdNew(userId)
            .flatMapSingle { user ->
                Single.create<Boolean> { emitter ->
                    val disposables = CompositeDisposable()
                    disposables += if (user.binds?.userFavorite == null) {
                        addUserToFavoriteRequest(user)
                            .subscribeSimple(
                                onError = { emitter.onError(it) },
                                onSuccess = { emitter.onSuccess(true) })
                    } else {
                        eventRepository.deleteFromFavorite(user.binds?.userFavorite?.id.toString())
                            .subscribeSimple(
                                onError = { emitter.onError(it) },
                                onComplete = { emitter.onSuccess(false) })
                    }
                    emitter.setDisposable(disposables)
                }
            }
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    onReceiveError(it)
                },
                onSuccess = {
                    viewState.updateUserSubscription(userId.toInt(), it)
                }
            )
    }

    override fun onSubscribeClick(action: UserSubscribeButton.Action) {
        compositeDisposable += organizationRepository.getOrganizationDetails(organizationId)
            .flatMapMaybe {
                Maybe.create<Boolean> { emitter ->
                    val disposables = CompositeDisposable()
                    disposables += if (it.binds?.userFavorite == null) {
                        addOrganizationToFavoriteRequest()
                            .subscribeSimple(
                                onError = { emitter.onError(it) },
                                onSuccess = { emitter.onSuccess(true) })
                    } else {
                        eventRepository.deleteFromFavorite(it.binds?.userFavorite?.id.toString())
                            .subscribeSimple(
                                onError = { emitter.onError(it) },
                                onComplete = { emitter.onSuccess(false) })
                    }
                    emitter.setDisposable(disposables)
                }
            }
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    onReceiveError(it)
                }, onSuccess = {
                    viewState.setSubscribed(it)
                })
    }

    override fun onActionCancel(event: String, registrationId: String?) {
        compositeDisposable += eventRepository.cancelRegisterToEvent(registrationId?.toInt() ?: 0)
            .andThen(eventRepository.getEventDetails(event))
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple {
                viewState.updateEvent(it.event)
            }
    }

    override fun onUserClick(user: String?) {
        if (appData.isCurrentUser(user.toString())) {
            viewState.showCurrentUser(appData.getUserNew().id.toString())
        } else {
            viewState.showUser(user.toString())
        }
    }

    override fun onActionRegister(event: String) = viewState.showEventRequest(event)
    override fun onShowMoreEventsClick() = viewState.showAllEvents(organizationId)
    override fun onShowMoreUsersClick() = viewState.showAllUsers(organizationId)
    override fun onShowEventClick(event: String) = viewState.showAboutEvent(event)
    override fun onRefreshRequest() = loadData(false)

    private fun organizationDataRequest(): Maybe<AboutOrganizationData> {
        val organization = organizationRepository.getOrganizationDetails(organizationId).toMaybe()
        val events = eventRepository.getOrganizationEventsListWithoutPagination(
            mapOf(
                EventNew.EVENT_ACTIVE to true,
                EventNew.EVENT_LIMIT to 3,
                EventNew.EVENT_OFFSET to 0,
                EventNew.EVENT_BINDS to "rights,organization,tag,page,activity,user-registration,user-form-result,userFavorite,current-user-registration,eventRegistrationState,current-user-registration-state",
                EventNew.EVENT_ORGANIZATION to organizationId,
                EventNew.EVENT_SORT_FIELD to "id"
            )
        )
        val members = organizationRepository.getOrganizationMembersWithoutPagination(
            mapOf(
                //OrganizationMember.MEMBERS_LIMIT to 3,
                OrganizationMember.MEMBERS_OFFSET to 0,
                OrganizationMember.MEMBERS_BINDS to "user,userFavorite",
                OrganizationMember.MEMBERS_ORGANIZATION to organizationId
            )
        )
        return Maybe.zip(
            organization,
            events,
            members
        ) { org, e, m ->
            val membersList = arrayListOf<OrganizationMemberModel>()
            m.forEach { member ->
                member.binds?.user?.binds =
                    UserBinds(userFavorite = member.binds?.userFavorite)
                membersList.add(member)
            }
            AboutOrganizationData(org, e.data, membersList.subList(0, 3), membersList.size)
        }
    }

    fun isCurrentUser(id: String): Boolean {
        return appData.isCurrentUser(id)
    }

    private fun addOrganizationToFavoriteRequest(): Single<AddFavoriteModel> {
        return eventRepository.addToFavorites(
            AddToFavoriteModel(
                appData.getId(),
                AddToFavoriteEntityModel(FAVORITE_ORGANIZATION, organizationId.toInt())
            )
        )
    }

    private fun addUserToFavoriteRequest(user: UserDetail): Single<AddFavoriteModel> {
        return eventRepository.addToFavorites(
            AddToFavoriteModel(
                appData.getId(),
                AddToFavoriteEntityModel(FAVORITE_SPEAKER, user.id)
            )
        )
    }
}