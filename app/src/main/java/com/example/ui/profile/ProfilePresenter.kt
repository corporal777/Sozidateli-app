package com.example.ui.profile

import android.app.NotificationManager
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.bodies.ConfirmCodeBody
import com.example.data.models.FieldDetails
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import performOnBackgroundOutOnMain
import ru.houseofapps.chat.HAChat
import withLoadingDialog
import java.lang.Exception
import javax.inject.Inject

@InjectViewState
class ProfilePresenter
@Inject constructor(
        private val userRepository: UserRepository,
        private val haChat: HAChat,
        private val appData: AppData,
        private val notificationManager: NotificationManager,
        private val authRepository: AuthRepository
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
        viewState.setUserState(appData.hasBaseState, appData.hasMaxState)
        try {
            viewState.setUser(appData.getUserNew())
        } catch (e: Exception) {
            compositeDisposable += userRepository.getUserShortNew()
                    .performOnBackgroundOutOnMain()
                    .subscribe({
                        viewState.setUser(appData.getUserNew())
                    }, { it.printStackTrace() })
        }
    }

    fun getUserData() = appData.getUserNew()

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

    override fun sendEmail(email: String) {
        compositeDisposable += authRepository.registerEmailResend(email)
                .performOnBackgroundOutOnMain()
                .subscribe({
                    viewState.hideDialogProgress()
                    appData.updateUserNew {
                        this.email = FieldDetails(email, null, true, false, false, null)
                    }
                    viewState.emailSuccess()
                }, {
                    viewState.hideDialogProgress()
                    it.printStackTrace()
                })
    }

    override fun sendPhone(phone: String) {
        compositeDisposable += authRepository.registerPhoneResend("personal", phone)
                .performOnBackgroundOutOnMain()
                .subscribe({
                    viewState.hideDialogProgress()
                    viewState.phoneSuccess(phone)
                }, {
                    viewState.hideDialogProgress()
                    it.printStackTrace()
                })
    }

    override fun confirmCode(phone: String, code: String) {
        compositeDisposable += authRepository.confirmPhone(ConfirmCodeBody("personal", phone, code))
                .performOnBackgroundOutOnMain()
                .subscribe({
                    viewState.hideDialogProgress()
                    viewState.codeSuccess()
                }, {
                    viewState.hideDialogProgress()
                    it.printStackTrace()
                })
    }

    override fun onSettingsClick() {
        viewState.showSettings()
    }
}
