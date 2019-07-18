package com.example.ui.profile

import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.Event
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class ProfilePresenter
@Inject constructor(
        private val userRepository: UserRepository,
        private val appData: AppData
) : BasePresenter<ProfileContract.View>(), ProfileContract.Presenter {
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.hideLastNotification()
        appData.notificationsCountSubject
                .performOnBackgroundOutOnMain()
                .subscribe({
                    val user = appData.getUser()
                    val notification = user.last_notification
                    if (notification != null) viewState.showLastNotification(notification.text, it)
                    else viewState.hideLastNotification()
                }, {

                }).call(compositeDisposable)


        userRepository.getUserShort()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    viewState?.apply {
                        setUser(appData.getUser())
                        it.last_notification?.let { notification ->
                            viewState.showLastNotification(notification.text, it.notification_unread)
                        }
                    }
                }, {
                    it.printStackTrace()
                })
                .call(compositeDisposable)
    }

    override fun attachView(view: ProfileContract.View?) {
        super.attachView(view)
        viewState?.apply {
            setUser(appData.getUser())
        }
    }

    override fun clickAboutStatus() = viewState.showAboutStatus()

    override fun clickFullProfile() = viewState.showFullProfile()

    override fun clickFavorite() = viewState.showFavorite()

    override fun clickMyEvents() = viewState.showMyEvents()

    override fun clickTabEvents() = viewState.showTabEvents()

    override fun clickCurrentEvent(event: Event) {
        appData.getUser().default_event?.let {
            viewState.showCurrentEvent(it)
        }
    }

    override fun clickAboutApp() = viewState.showAboutApp()

    override fun clickChatSetting() = viewState.showChatSetting()

    override fun onNotificationClick() = viewState.showNotifications()

    override fun onShowBannedClick() = viewState.showBanned()
}
