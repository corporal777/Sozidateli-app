package com.example.ui.organizations

import android.graphics.Bitmap
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.UserEventData
import com.example.data.bodies.AddToFavoriteEntityModel
import com.example.data.bodies.AddToFavoriteModel
import com.example.data.models.*
import com.example.data.models.user.User
import com.example.repository.EventRepository
import com.example.repository.OrganizationRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.loadBitmap
import io.reactivex.Maybe
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class OrganizationPresenter
@Inject constructor(
        private val appData: AppData,
        private val organizationRepository: OrganizationRepository,
        private val userRepository: UserRepository,
        private val eventRepository: EventRepository,
        private val eventData: UserEventData
) : BasePresenter<OrganizationContract.View>(), OrganizationContract.Presenter {

    lateinit var organizationId: String
    lateinit var organization: OrganizationNew
    private var scroll = 0
    private var firstLaunch = true

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadData(true)
    }

    private fun loadData(withLoadingPlaceholder: Boolean) {
        /*compositeDisposable += organizationRepository.getOrganizationById(organizationId)
                .performOnBackgroundOutOnMain()
                .flatMap {
                    Maybe.zip(it.organization.logo.loadBitmap(), it.organization.background.loadBitmap(), BiFunction<Optional<Bitmap>, Optional<Bitmap>, OrganizationDataAndImages> { logo, bg ->
                        OrganizationDataAndImages(it, logo.value, bg.value)
                    })
                            .toSingle()
                }
                .let {
                    if (withLoadingPlaceholder) it.withLoadingDialog(viewState)
                    else it
                }
                .subscribe({
                    val organizationData = it.data
                    val uid = appData.getUser().user_id
                    organizationData.members.forEach { member ->
                        member.user?.isCurrentUser = member.user?.user_id == uid
                    }
                    viewState.setOrganization(
                            it.logo,
                            it.background,
                            organizationData.organization,
                            organizationData.events,
                            organizationData.members
                    )
                    viewState.setSubscribed(organizationData.organization.isSubscribed ?: false)
                }, {
                    it.printStackTrace()
                })*/
        compositeDisposable += organizationRepository.getOrganizationDetails(organizationId)
                .performOnBackgroundOutOnMain()
                .flatMap {
                    Maybe.zip(it.logo?.uri.loadBitmap(), it.image?.uri.loadBitmap(), BiFunction<Optional<Bitmap>, Optional<Bitmap>, OrganizationDataAndImages> { logo, bg ->
                        OrganizationDataAndImages(it, logo.value, bg.value)
                    })
                            .toSingle()
                }
                .let {
                    if (withLoadingPlaceholder) it.withLoadingDialog(viewState)
                    else it
                }
                .subscribe({ org ->
                    org.data.binds?.membersSize = org.data.binds?.member?.size
                    compositeDisposable += organizationRepository.getOrganizationMembersWithoutPagination(
                            mutableMapOf<String, Any>().apply {
                                put(OrganizationMember.MEMBERS_LIMIT, 3)
                                put(OrganizationMember.MEMBERS_OFFSET, 0)
                                put(OrganizationMember.MEMBERS_BINDS, "user,userFavorite")
                                put(OrganizationMember.MEMBERS_ORGANIZATION, organizationId)
                            }).performOnBackgroundOutOnMain()
                            .subscribe({
                                it.forEach { member ->
                                    member.binds?.user?.binds = UserBinds(userFavorite = member.binds?.userFavorite)
                                }
                                org.data.binds?.member = it

                                compositeDisposable += eventRepository.getEventsListWithoutPagination(
                                        mutableMapOf<String, Any>().apply {
                                            put(EventNew.EVENT_LIMIT, 3)
                                            put(EventNew.EVENT_SORT_TYPE, "desc")
                                            put(EventNew.EVENT_OFFSET, 0)
                                            put(EventNew.EVENT_BINDS, "rights,organization,tag,page,activity,user-registration,user-form-result")
                                            put(EventNew.EVENT_ORGANIZATION, organizationId)
                                        }
                                ).performOnBackgroundOutOnMain()
                                        .subscribe({ event ->
                                            org.data.binds?.events = event.data
                                            org.data.binds?.eventsSize = event.totalCount
                                            setOrganizationData(org)
                                        }, {
                                            setOrganizationData(org)
                                            it.printStackTrace()
                                        })
                            }, {
                                setOrganizationData(org)
                                it.printStackTrace()
                            })
                }, {
                    it.printStackTrace()
                })
    }

    private fun getEvents() {
        compositeDisposable += eventRepository.getEventsListWithoutPagination(
                mutableMapOf<String, Any>().apply {
                    put(EventNew.EVENT_LIMIT, 3)
                    put(EventNew.EVENT_OFFSET, 0)
                    put(EventNew.EVENT_SORT_TYPE, "desc")
                    put(EventNew.EVENT_BINDS, "rights,organization,tag,page,activity,user-registration,user-form-result")
                    put(EventNew.EVENT_ORGANIZATION, organizationId)
                }
        ).performOnBackgroundOutOnMain()
                .subscribe({

                }, {
                    it.printStackTrace()
                })
    }

    private fun setOrganizationData(it: OrganizationDataAndImages) {
        val organizationData = it.data
        val uid = appData.getId()
        organizationData.binds?.member?.forEach { member ->
            member.isCurrentUser = member.user == uid
        }
        organization = organizationData
        viewState.setOrganization(
                it.logo,
                it.background,
                organizationData
                /*organizationData.organization,
                organizationData.events,
                organizationData.members*/
        )
        viewState.setSubscribed(organizationData.binds?.userFavorite != null)
    }

    override fun attachView(view: OrganizationContract.View?) {
        super.attachView(view)
        viewState.changeScrollY(scroll)
        if (firstLaunch) firstLaunch = false
        else loadData(true)
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

    override fun onSubscribeClick() {
        compositeDisposable += eventRepository.addToFavorites(AddToFavoriteModel(appData.getId(), AddToFavoriteEntityModel(AddToFavoriteEntityModel.FAVORITE_ORGANIZATION, organizationId.toInt())))
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple {
                    organization.binds?.userFavorite = EventUserFavorite(it.id, it.user)
                    viewState.setSubscribed(true)
                }
    }

    override fun onUnsubscribeClick() {
        compositeDisposable += eventRepository.deleteFromFavorite(organization.binds?.userFavorite?.id.toString())
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple {
                    organization.binds?.userFavorite = null
                    viewState.setSubscribed(false)
                }
    }

    override fun onShowMoreUsersClick() {
        viewState.showUsers(organizationId)
    }

    override fun onUserClick(user: UserDetail?/*User*/) {
        viewState.showUser(user?.id.toString()/*user.user_id.toString()*/)
    }

    override fun onUserActionCLick(user: UserDetail?/*User*/) {
        val isSubscribed = user?.binds?.userFavorite != null
        if (isSubscribed) {
            compositeDisposable += eventRepository.deleteFromFavorite(user?.binds?.userFavorite?.id.toString())
                    .performOnBackgroundOutOnMain()
                    .withLoadingDialog(viewState)
                    .subscribeSimple {
                        user?.binds?.userFavorite = null
                        viewState.updateUser(user)
                    }
        } else {
            compositeDisposable += eventRepository.addToFavorites(AddToFavoriteModel(appData.getId(), AddToFavoriteEntityModel(AddToFavoriteEntityModel.FAVORITE_SPEAKER, user?.id)))
                    .performOnBackgroundOutOnMain()
                    .withLoadingDialog(viewState)
                    .subscribeSimple {
                        user?.binds?.userFavorite = EventUserFavorite(it.id, it.user)
                        viewState.updateUser(user)
                    }
        }
    }

    override fun onActionRegister(event: String) {
        /*compositeDisposable += eventRepository.eventRegisterCheck(event)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple(
                        onError = {
                            checkRegistrationFields(event, emptyList())
                        },
                        onSuccess = {
                            checkRegistrationFields(event, it)
                        }
                )*/
        compositeDisposable += eventRepository.checkUserProfile()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple(
                        onError = {
                            checkRegistrationFields(event, emptyList())
                        },
                        onSuccess = {
                            checkRegistrationFields(event, it.fields?: emptyList())
                        }
                )
    }

    private fun checkRegistrationFields(event: String, fields: List<UserProfileFields/*EventRegisterCheckField*/>) {
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
                .withLoadingDialog(viewState)
                .subscribeSimple { loadData(false) }
        /*compositeDisposable += eventRepository.eventRegisterCancel(event)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple { loadData(false) }*/
    }

    override fun onActionWriteToOrganization(emails: List<EventPhoneModel/*EmailAffiliation*/>) {
        if (!emails.isNullOrEmpty()) viewState.showWriteToOrganizationEmails(emails)
    }

    override fun onWriteToOrganizationEmailChosen(email: EventPhoneModel/*EmailAffiliation*/) {
        viewState.showWriteToOrganization(email)
    }

    override fun onActionShowEvent(event: String) {
        compositeDisposable += userRepository.getUserShortNew().ignoreElement().onErrorComplete()
                .andThen(eventData.load(event))
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple { viewState.selectEvent() }
        /*compositeDisposable += eventRepository.setDefaultEvent(event)
                .andThen(userRepository.getUserShortNew().ignoreElement().onErrorComplete())
                .andThen(eventData.load(event))
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple { viewState.selectEvent() }*/
    }

    override fun onShowEventClick(event: String) = viewState.showAboutEvent(event)

    override fun onShowFilterClick(format: Int) = viewState.showSearch(format)

    override fun onRefreshRequest() {
        loadData(false)
    }

    private class OrganizationDataAndImages(
            val data: OrganizationNew/*OrganizationData*/,
            val logo: Bitmap?,
            val background: Bitmap?
    )
}
