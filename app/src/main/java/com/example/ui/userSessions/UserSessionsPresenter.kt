package com.example.ui.userSessions

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.UserSessionModel
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCustomProgressBarLoadingDialog
import javax.inject.Inject

@InjectViewState
class UserSessionsPresenter
@Inject constructor(
    private val userRepository: UserRepository,
    private val appData: AppData,
    private val authRepository: AuthRepository
) : BasePresenter<UserSessionsContract.View>(appData), UserSessionsContract.Presenter {

    private val deviceId = appData.deviceId ?: ""

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.showSessionsLoadingPlaceholder()
        compositeDisposable += userRepository.getAllUsersSessions(deviceId)
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                viewState.apply {
                    setCurrentSession(it.currentSession)
                    setOtherSessions(it.userSessions)
                }
            }
    }


    override fun killAllSessionsClick() {
        compositeDisposable += userRepository.killAllUsersOtherSessions()
            .andThen(userRepository.getAllUsersSessions(deviceId))
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple {
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
            }
    }
}