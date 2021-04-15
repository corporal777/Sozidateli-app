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
    private var scroll = 0

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadData(true)
    }

    private fun loadData(withLoadingPlaceholder: Boolean) {
        compositeDisposable += organizationRepository.getOrganizationById(organizationId)
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
                })
        /*compositeDisposable += organizationRepository.getOrganizationDetails(organizationId)
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
                .subscribe({
                    val organizationData = it.data
                    val uid = appData.getId()
                    organizationData.binds?.member?.forEach { member ->
                        //member.user?.isCurrentUser = member.user == uid
                    }
                    viewState.setOrganization(
                            it.logo,
                            it.background,
                            organizationData
                            /*organizationData.organization,
                            organizationData.events,
                            organizationData.members*/
                    )
                    viewState.setSubscribed(organizationData.binds?.userFavorite != null)
                }, {
                    it.printStackTrace()
                })*/
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

    override fun onSubscribeClick() {
        compositeDisposable += organizationRepository.subscribe(organizationId)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    viewState.setSubscribed(true)
                }, {
                    it.printStackTrace()
                })
    }

    override fun onUnsubscribeClick() {
        compositeDisposable += organizationRepository.unsubscribe(organizationId)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    viewState.setSubscribed(false)
                }, {
                    it.printStackTrace()
                })
    }

    override fun onShowMoreUsersClick() {
        viewState.showUsers(organizationId)
    }

    override fun onUserClick(user: /*UserDetail*/User) {
        viewState.showUser(/*user.id.toString()*/user.user_id.toString())
    }

    override fun onUserActionCLick(user:/* UserDetail*/User) {
        val id = user.user_id.toString()/*user.id.toString()*/
        val request = if (user.is_in_favorite) userRepository.removeFromFavorite(id)
        else userRepository.addToFavorite(id)
        compositeDisposable += request
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple {
                    user.is_in_favorite = !user.is_in_favorite
                    viewState.updateUser(user)
                }
    }

    override fun onActionRegister(event: String) {
        compositeDisposable += eventRepository.eventRegisterCheck(event)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple(
                        onError = {
                            checkRegistrationFields(event, emptyList())
                        },
                        onSuccess = {
                            checkRegistrationFields(event, it)
                        }
                )
        /*compositeDisposable += eventRepository.checkUserProfile()
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
    }

    private fun checkRegistrationFields(event: String, fields: List</*UserProfileFields*/EventRegisterCheckField>) {
        //val filtered = fields.filter { it.value == false }.mapNotNull { it.name }
        val filtered = fields.mapNotNull { it.title }
        if (filtered.isEmpty()) {
            viewState.showEventRequest(event)
        } else {
            viewState.showRegistrationFieldsRequest(filtered)
        }
    }

    override fun onShowEditProfileClick() {
        viewState.showEditProfile(appData.getUser().user_id.toString())
    }

    override fun onActionCancel(event: String) {
        compositeDisposable += eventRepository.eventRegisterCancel(event)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple { loadData(false) }
    }

    override fun onActionWriteToOrganization(emails: List</*EventPhoneModel*/EmailAffiliation>) {
        if (!emails.isNullOrEmpty()) viewState.showWriteToOrganizationEmails(emails)
    }

    override fun onWriteToOrganizationEmailChosen(email: /*EventPhoneModel*/EmailAffiliation) {
        viewState.showWriteToOrganization(email)
    }

    override fun onActionShowEvent(event: String) {
        /*compositeDisposable += userRepository.getUserShortNew().ignoreElement().onErrorComplete()
                .andThen(eventData.load(event))
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple { viewState.selectEvent() }*/
        compositeDisposable += eventRepository.setDefaultEvent(event)
                .andThen(userRepository.getUserShortNew().ignoreElement().onErrorComplete())
                .andThen(eventData.load(event))
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple { viewState.selectEvent() }
    }

    override fun onShowEventClick(event: String) = viewState.showAboutEvent(event)

    override fun onShowFilterClick(format: Int) = viewState.showSearch(format)

    override fun onRefreshRequest() {
        loadData(false)
    }

    private class OrganizationDataAndImages(
            val data: /*OrganizationNew*/OrganizationData,
            val logo: Bitmap?,
            val background: Bitmap?
    )
}
