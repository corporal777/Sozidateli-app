package com.example.ui.organizations

import android.graphics.Bitmap
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.UserEventData
import com.example.data.models.EmailAffiliation
import com.example.data.models.Event
import com.example.data.models.Optional
import com.example.data.models.OrganizationData
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
                    organizationData.members.forEach { member -> member.user.isCurrentUser = member.user.user_id == uid }
                    viewState.setOrganization(it.logo, it.background, organizationData.organization, organizationData.events, organizationData.members)
                    viewState.setSubscribed(organizationData.organization.isSubscribed ?: false)
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

    override fun onUserClick(user: User) {
        viewState.showUser(user.user_id.toString())
    }

    override fun onUserActionCLick(user: User) {
        val id = user.user_id.toString()
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

    override fun onActionRegister(event: String) = viewState.showEventRequest(event)

    override fun onActionCancel(event: String) {
        compositeDisposable += eventRepository.eventRegisterCancel(event)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple { loadData(false) }
    }

    override fun onActionWriteToOrganization(emails: List<EmailAffiliation>) {
        if (!emails.isNullOrEmpty()) viewState.showWriteToOrganizationEmails(emails)
    }

    override fun onWriteToOrganizationEmailChosen(email: EmailAffiliation) {
        viewState.showWriteToOrganization(email)
    }

    override fun onActionShowEvent(event: String) {
        compositeDisposable += eventRepository.setDefaultEvent(event)
                .andThen(userRepository.getUserShort().ignoreElement().onErrorComplete())
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
            val data: OrganizationData,
            val logo: Bitmap?,
            val background: Bitmap?
    )
}
