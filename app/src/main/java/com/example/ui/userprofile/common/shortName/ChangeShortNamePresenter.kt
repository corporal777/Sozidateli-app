package com.example.ui.userprofile.common.shortName

import android.app.NotificationManager
import com.example.data.AppData
import com.example.data.models.UserDetail
import com.example.data.socket.SocketIOManager
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.ui.base.bottomSheet.BaseBottomSheetPresenter
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import withCustomLoading
import withProgressBarDialogLoading
import javax.inject.Inject

@InjectViewState
class ChangeShortNamePresenter
@Inject constructor(
    private val userRepository: UserRepository,
    private val appData: AppData,
) : BasePresenter<ChangeShortNameContract.View>(appData), ChangeShortNameContract.Presenter {

    private val userId = appData.getUser().id.toString()
    private var userShortName = appData.getUser().shortName

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setUserShortName(userShortName, userId)
        performDataChange()
    }

    override fun onChangeShortName(short: String) {
        userShortName = short
        performDataChange()
        compositeDisposable += userRepository.getUserByShortName(userShortName!!)
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    viewState.setUserShortNameUnique(true, if (isSameShortName()) null else userShortName)
                },
                onSuccess = {
                    viewState.setUserShortNameUnique(false, if (isSameShortName()) null else userShortName)
                }
            )
    }

    override fun onSaveShortName() {
        compositeDisposable += userRepository.updateUserProfileField(
            mapOf(UserDetail.USER_SHORT_NAME to userShortName)
        ).doOnSuccess { new -> appData.updateUser { this.shortName = new.shortName } }
            .performOnBackgroundOutOnMain()
            .withCustomLoading(viewState)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = { viewState.navigateUp() }
            )
    }

    private fun isSameShortName() = userShortName == appData.getUser().shortName
    private fun performDataChange() {
        viewState.enableBtnSave(!userShortName.isNullOrEmpty() && !isSameShortName())
    }
}