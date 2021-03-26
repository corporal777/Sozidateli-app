package com.example.ui.profile

import android.app.NotificationManager
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
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
        compositeDisposable += userRepository.getUserShortNew()
                .performOnBackgroundOutOnMain()
                .subscribe({
                    viewState.setUser(it)
                    getAdditionalData()
                }, { it.printStackTrace() })

    }

    override fun attachView(view: ProfileContract.View?) {
        super.attachView(view)
        viewState.setUser(appData.getUserNew())
    }

    override fun onProfileClick() = viewState.showProfile(appData.getId().toString())

    override fun onFavoritesClick() = viewState.showFavorites()

    override fun onEventsClick() = viewState.showEvents()

    override fun onAboutApplicationClick() = viewState.showAboutApp()

    override fun onBannedClick() = viewState.showBanned()

    override fun onSupportClick() {
        viewState.openSupportEmail(appData.getId().toString())
    }

    override fun onRateClick() {
        viewState.openPlayMarket()
    }

    override fun onLogoutClick() {
        compositeDisposable += userRepository.logout(appData.getId())
                .doOnComplete {
                    appData.isSubscribedToPush = false
                    haChat.disconnect()
                    appData.logout()
                    notificationManager.cancelAll()
                }
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeBy(
                        onError = {
                            it.printStackTrace()
                            viewState.showRequestErrorMessage()
                        }
                )
        /*compositeDisposable += userRepository.getFcmToken()
                .flatMapCompletable { userRepository.notificationsUnregister(it.token) }
                .doOnComplete {
                    appData.isSubscribedToPush = false
                    haChat.disconnect()
                    appData.logout()
                    notificationManager.cancelAll()
                }
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeBy(
                        onError = {
                            it.printStackTrace()
                            viewState.showRequestErrorMessage()
                        }
                )*/
    }

    private fun getAdditionalData() {
        compositeDisposable += userRepository.getEducationLevel()
                .performOnBackgroundOutOnMain()
                .subscribe({}, { it.printStackTrace() })

        compositeDisposable += userRepository.getSpeciality()
                .performOnBackgroundOutOnMain()
                .subscribe({}, { it.printStackTrace() })

        compositeDisposable += userRepository.getAcademicDegrees()
                .performOnBackgroundOutOnMain()
                .subscribe({}, { it.printStackTrace() })
    }

    override fun onSettingsClick() {
        viewState.showSettings()
    }
}
