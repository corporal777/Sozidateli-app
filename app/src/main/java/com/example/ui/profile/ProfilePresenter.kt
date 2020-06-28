package com.example.ui.profile

import android.app.NotificationManager
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
        private val appData: AppData,
        private val notificationManager: NotificationManager
) : BasePresenter<ProfileContract.View>(), ProfileContract.Presenter {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += userRepository.getUserFull()
                .performOnBackgroundOutOnMain()
                .subscribe({
                    viewState.setUser(it)
                }, { it.printStackTrace() })
    }

    override fun attachView(view: ProfileContract.View?) {
        super.attachView(view)
        viewState.setUser(appData.getUser())
    }

    override fun onProfileClick() = viewState.showProfile(appData.getUser().user_id.toString())

    override fun onFavoritesClick() = viewState.showFavorites()

    override fun onEventsClick() = viewState.showEvents()

    override fun onAboutApplicationClick() = viewState.showAboutApp()

    override fun onBannedClick() = viewState.showBanned()

    override fun onSupportClick() {
        viewState.openSupportEmail(appData.getUser().user_id.toString())
    }

    override fun onRateClick() {
        viewState.openPlayMarket()
    }

    override fun onLogoutClick() {
        compositeDisposable += userRepository.getFcmToken()
                .flatMapCompletable { userRepository.notificationsUnregister(it.token) }
                .doOnComplete {
                    appData.isSubscribedToPush = false
                    haChat.disconnect()
                    appData.logout()
                    notificationManager.cancelAll()
                }
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({}, {})
    }
}
