package com.example.ui.userSessions

import android.app.NotificationManager
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.UserSessionModel
import com.example.data.socket.SocketIOManager
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.ui.profile.ProfileContract
import com.example.ui.userSessions.items.SessionsAction
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withProgressBarLoadingDialog
import javax.inject.Inject

@InjectViewState
class UserSessionsPresenter
@Inject constructor(
    private val userRepository: UserRepository,
    private val appData: AppData,
    private val authRepository: AuthRepository
) : BasePresenter<UserSessionsContract.View>(appData), UserSessionsContract.Presenter {

    var deviceId = ""
    private var isShownAll = false
    private val allOtherSessions = arrayListOf<UserSessionModel>()
    private val shortAllOtherSessions = arrayListOf<UserSessionModel>()
    private var actionType = SessionsAction.HIDDEN

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.showSessionsLoadingPlaceholder()
        getUserSessionsData()
    }

    private fun getUserSessionsData() {
        compositeDisposable += userRepository.getAllUsersSessions(deviceId)
            .doOnSuccess {
                allOtherSessions.addAll(it)
                if (it.size > 3) {
                    for (i in 0 until 3) {
                        shortAllOtherSessions.add(it[i])
                    }
                }
            }
            .performOnBackgroundOutOnMain()
            .subscribeSimple { _ ->
                compositeDisposable += userRepository.getAllUsersSessionsFromCurrentDevice(deviceId)
                    .performOnBackgroundOutOnMain()
                    .subscribeSimple { currentSessions ->
                        viewState.setCurrentSession(currentSessions.userSessions[0])
                        if (allOtherSessions.size > 3) {
                            viewState.showSessionsActionButton(actionType)
                            viewState.setOtherSessions(shortAllOtherSessions)
                        }else {
                            viewState.setOtherSessions(allOtherSessions)
                        }
                    }
            }
    }

    override fun killAllSessionsClick() {
        compositeDisposable += userRepository.killAllUsersOtherSessions()
            .andThen(userRepository.getAllUsersSessions(deviceId))
            .performOnBackgroundOutOnMain()
            .withProgressBarLoadingDialog(viewState)
            .subscribeSimple {
                viewState.hideSessionsActionButton()
                viewState.setOtherSessions(it)
            }
    }

    override fun killUsersDeviceSessionClick(id: Int) {
        compositeDisposable += userRepository.killUsersDeviceSession(id)
            .andThen(userRepository.getAllUsersSessions(deviceId))
            .performOnBackgroundOutOnMain()
            .withProgressBarLoadingDialog(viewState)
            .subscribeSimple {
                viewState.setOtherSessions(it)
                if (it.size <= 3){
                    viewState.hideSessionsActionButton()
                }
            }
    }

    override fun showSessionClick(session: UserSessionModel) {
        viewState.showSessionBottomSheetDialog(session)
    }

    override fun showOrHideSessionsHistoryClick(action: SessionsAction) {
        viewState.apply {
            if (action == SessionsAction.SHOWN) {
                setOtherSessions(allOtherSessions)
            } else {
                setOtherSessions(shortAllOtherSessions)
            }
        }
    }


}