package com.example.ui.profile.shortName

import android.app.NotificationManager
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.UserDetail
import com.example.data.socket.SocketIOManager
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.base.bottomSheet.BaseBottomSheetPresenter
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withProgressBarDialogLoading
import javax.inject.Inject

@InjectViewState
class ChangeShortNamePresenter
@Inject constructor(
    private val userRepository: UserRepository,
    private val appData: AppData,
    private val socket: SocketIOManager,
    private val notificationManager: NotificationManager,
    private val authRepository: AuthRepository,
) : BaseBottomSheetPresenter<ChangeShortNameContract.View>(appData),
    ChangeShortNameContract.Presenter {

    var userId = ""
    var userShortName = ""

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        val userShortName = if (!userShortName.isNullOrEmpty()) {
            if (userShortName == userId) "sozidateli.ru/id$userShortName"
            else "sozidateli.ru/$userShortName"
        } else "sozidateli.ru/id$userId"

        viewState.setUserShortName(userShortName)
    }

    override fun checkUserShortNameUnique(short: String) {
        compositeDisposable += userRepository.getUserByShortName(short)
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { viewState.setUserShortNameUnique(true) },
                onSuccess = { viewState.setUserShortNameUnique(false) }
            )
    }

    override fun updateUserShortName(short: String) {
        compositeDisposable += userRepository.updateUserProfileField(mapOf(UserDetail.USER_SHORT_NAME to short))
            .doOnSuccess { new ->
                appData.updateUserNew { this.shortName = new.shortName }
            }
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple(
                onError = {
                    onReceiveError(it)
                    viewState.hideBottomSheetDialog()
                },
                onSuccess = { new ->
                    viewState.apply {
                        updateUserShortNameInProfile(new)
                        hideBottomSheetDialog()
                        showUserShortNameSuccessUpdated()
                    }
                })
    }


}