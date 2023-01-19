package com.example.ui.userSessions

import android.app.NotificationManager
import android.util.Log
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
import withCustomProgressBarLoadingDialog
import withProgressBarLoadingDialog
import javax.inject.Inject
import kotlin.math.abs

@InjectViewState
class UserSessionsPresenter
@Inject constructor(
    private val userRepository: UserRepository,
    private val appData: AppData,
    private val authRepository: AuthRepository
) : BasePresenter<UserSessionsContract.View>(appData), UserSessionsContract.Presenter {

    var deviceId = appData.deviceId?:""
    private val allOtherSessions = arrayListOf<UserSessionModel>()
    private val shortAllOtherSessions = arrayListOf<UserSessionModel>()
    private var actionType = SessionsAction.HIDDEN
    private var mDy = 0f

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.showSessionsLoadingPlaceholder()
        getUserSessionsData()
    }

    override fun attachView(view: UserSessionsContract.View?) {
        super.attachView(view)
        viewState.setAppBarElevation(mDy)
    }

    override fun changeAppBarElevation(value: Int) {
        mDy = abs(value / 10f)
        viewState.setAppBarElevation(mDy)
    }

    private fun getUserSessionsData() {
        compositeDisposable += userRepository.getAllUsersSessions(deviceId)
            .doOnSuccess {
                allOtherSessions.addAll(it.userSessions)
                if (it.userSessions.size > 3) {
                    for (i in 0 until 3) {
                        shortAllOtherSessions.add(it.userSessions[i])
                    }
                }
            }
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                viewState.setCurrentSession(it.currentSession)
                if (allOtherSessions.size > 3) {
                    viewState.showSessionsActionButton(actionType)
                    viewState.setOtherSessions(shortAllOtherSessions)
                }else {
                    viewState.setOtherSessions(allOtherSessions)
                }
            }
    }

    override fun killAllSessionsClick() {
        compositeDisposable += userRepository.killAllUsersOtherSessions()
            .andThen(userRepository.getAllUsersSessions(deviceId))
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple {
                viewState.hideSessionsActionButton()
                viewState.setOtherSessions(it.userSessions)
            }
    }

    override fun killUsersDeviceSessionClick(id: Int) {
        compositeDisposable += userRepository.killUsersDeviceSession(id)
            .andThen(userRepository.getAllUsersSessions(deviceId))
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple {
                viewState.setOtherSessions(it.userSessions)
                if (it.userSessions.size <= 3){
                    viewState.hideSessionsActionButton()
                }
            }
    }

    override fun showSessionClick(session: UserSessionModel) {
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