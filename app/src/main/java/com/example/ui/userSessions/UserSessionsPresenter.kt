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
        getUserSessionsData()
    }

    private fun getUserSessionsData() {
        viewState.setOtherSessions(List(20) { null })
        compositeDisposable += userRepository.getAllUsersSessions()
            .doOnSuccess {
                allOtherSessions.addAll(it.userSessions)
                if (it.userSessions.size > 3) {
                    for (i in 0 until 3) {
                        shortAllOtherSessions.add(it.userSessions[i])
                    }
                }
            }
            .performOnBackgroundOutOnMain()
            .subscribeSimple { allSessions ->
                compositeDisposable += userRepository.getAllUsersSessionsFromCurrentDevice(deviceId)
                    .performOnBackgroundOutOnMain()
                    .subscribeSimple { currentSessions ->
                        viewState.setCurrentSession(currentSessions.userSessions[0])
                        if (allSessions.userSessions.size > 3) {
                            viewState.showSessionsActionButton(actionType)
                            viewState.setOtherSessions(shortAllOtherSessions)
                        }else {
                            viewState.setOtherSessions(allSessions.userSessions)
                        }
                    }
            }
    }

    override fun killAllSessionsClick() {
        compositeDisposable += userRepository.killAllUsersOtherSessions()
            .andThen(userRepository.getAllUsersSessions())
            .performOnBackgroundOutOnMain()
            .withProgressBarLoadingDialog(viewState)
            .subscribeSimple {
                viewState.hideSessionsActionButton()
                viewState.updateOtherSessions(it.userSessions)
            }
    }

    override fun killUsersDeviceSessionClick(id: Int) {
        compositeDisposable += userRepository.killUsersDeviceSession(id)
            .andThen(userRepository.getAllUsersSessions())
            .performOnBackgroundOutOnMain()
            .withProgressBarLoadingDialog(viewState)
            .subscribeSimple {
                viewState.updateOtherSessions(it.userSessions)
            }
    }

    override fun showSessionClick(session: UserSessionModel) {
        viewState.showSessionBottomSheetDialog(session)
    }

    override fun showOrHideSessionsHistoryClick(action: SessionsAction) {
        viewState.apply {
            if (action == SessionsAction.SHOWN) {
                updateOtherSessions(allOtherSessions)
            } else {
                updateOtherSessions(shortAllOtherSessions)
            }
        }
    }


}