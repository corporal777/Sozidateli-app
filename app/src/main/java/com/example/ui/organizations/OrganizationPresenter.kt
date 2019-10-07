package com.example.ui.organizations

import com.arellomobile.mvp.InjectViewState
import com.example.data.models.Event
import com.example.data.models.user.User
import com.example.repository.OrganizationRepository
import com.example.ui.base.BasePresenter
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class OrganizationPresenter
@Inject constructor(
        private val organizationRepository: OrganizationRepository
) : BasePresenter<OrganizationContract.View>(), OrganizationContract.Presenter {

    lateinit var organizationId: String
    private var scroll = 0

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += organizationRepository.getOrganizationById(organizationId)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    viewState.setOrganization(it.organization, it.events, it.members)
                    viewState.setSubscribed(it.organization.isSubscribed ?: false)
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
}
