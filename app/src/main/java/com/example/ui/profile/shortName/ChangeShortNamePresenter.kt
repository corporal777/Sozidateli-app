package com.example.ui.profile.shortName

import android.app.NotificationManager
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.bodies.UserShortNameBody
import com.example.data.socket.SocketIOManager
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.ui.base.bottomSheet.BaseBottomSheetContract
import com.example.ui.base.bottomSheet.BaseBottomSheetPresenter
import com.example.ui.profile.ProfileContract
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCustomProgressBarLoadingDialog
import javax.inject.Inject

@InjectViewState
class ChangeShortNamePresenter
@Inject constructor(
    private val userRepository: UserRepository,
    private val appData: AppData,
    private val socket: SocketIOManager,
    private val notificationManager: NotificationManager,
    private val authRepository: AuthRepository,
) : BaseBottomSheetPresenter<ChangeShortNameContract.View>(appData), ChangeShortNameContract.Presenter {

    override fun checkUserShortNameUnique(short: String) {
        compositeDisposable += userRepository.getUserByShortName(short)
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                    viewState.setUserShortNameUnique(true)
                }, onSuccess = {
                    viewState.setUserShortNameUnique(false)
                })
    }

   override fun updateUserShortName(userId: Int, short: String) {
        compositeDisposable += userRepository.updateUserShortName(userId, UserShortNameBody(short))
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    onReceiveError(it)
                    viewState.hideBottomSheetDialog()
                },
                onSuccess = { new ->
                    viewState.apply {
                        appData.updateUserNew {
                            this.shortName = new.shortName
                        }
                        updateUserShortNameInProfile(new)
                        hideBottomSheetDialog()
                        showUserShortNameSuccessUpdated()
                    }
                })
    }


}