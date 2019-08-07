package com.example.ui.profile

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import ru.houseofapps.chat.HAChat
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class ProfilePresenter
@Inject constructor(
        private val userRepository: UserRepository,
        private val haChat: HAChat,
        private val appData: AppData
) : BasePresenter<ProfileContract.View>(), ProfileContract.Presenter {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += appData.notificationsCountSubject
                .performOnBackgroundOutOnMain()
                .subscribe({ updateNotification() }, {})

        compositeDisposable += userRepository.getUserShort()
                .performOnBackgroundOutOnMain()
                .subscribe({
                    viewState.setUser(it)
                    updateNotification()
                }, { it.printStackTrace() })
    }

    private fun updateNotification() {
        val unreadNotifications = appData.getUser().notification_unread
        viewState.apply {
            if (unreadNotifications > 0) highlightNotifications(unreadNotifications)
            else hideLastNotification()
        }
    }

    override fun attachView(view: ProfileContract.View?) {
        super.attachView(view)
        viewState.setUser(appData.getUser())
        updateNotification()
    }

    override fun onProfileClick() = viewState.showProfile()

    override fun onFavoritesClick() = viewState.showFavorites()

    override fun onEventsClick() = viewState.showEvents()

    override fun onAboutApplicationClick() = viewState.showAboutApp()

    override fun onNotificationClick() = viewState.showNotifications()

    override fun onBannedClick() = viewState.showBanned()

    override fun onSupportClick() {
        TODO("not implemented")
    }

    override fun onRateClick() {
        TODO("not implemented")
    }

    override fun onLogoutClick() {
        compositeDisposable += userRepository.getFcmToken()
                .flatMapCompletable { userRepository.notificationsUnregister(it.token) }
                .doOnComplete {
                    appData.isSubscribedToPush = false
                    haChat.disconnect()
                    appData.logout()
                }
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({}, {})
    }
}
