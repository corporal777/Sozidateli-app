package com.example.ui.accountChange

import android.app.NotificationManager
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.UserDetail
import com.example.data.models.UserSessionModel
import com.example.data.socket.SocketIOManager
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import io.reactivex.Completable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import performOnBackgroundOutOnMain
import withCustomProgressBarLoadingDialog
import javax.inject.Inject
import kotlin.math.abs

@InjectViewState
class ChangeAccountPresenter
@Inject constructor(
    private val userRepository: UserRepository,
    private val appData: AppData,
    private val socket: SocketIOManager,
    private val notificationManager: NotificationManager
) : BasePresenter<ChangeAccountContract.View>(appData), ChangeAccountContract.Presenter {

    var mDeviceId = ""
    private var mDy = 0
    private val loggedSessions = arrayListOf<UserSessionModel>()
    private val unLoggedSessions = arrayListOf<UserSessionModel>()
    private var canShowMenu = true
    private var currentUserId = ""

    override fun changeAppBarElevation(value: Int) {
        mDy += value
        viewState.setAppBarElevation(abs(mDy / 10f))
    }

    override fun attachView(view: ChangeAccountContract.View?) {
        super.attachView(view)
        if (appData.isLoggedOut) {
            viewState.disableBackClick()
        }
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        currentUserId = appData.getId().toString()
        viewState.setAppBarElevation(0f)
        viewState.showProgressLoading()
        loadData()
    }

    private fun loadData() {
        compositeDisposable += userRepository.getAllUsersSessionsFromCurrentDevice(mDeviceId)
            .doOnSuccess {
                transformData(it.userSessions)
            }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    onReceiveError(it)
                    viewState.hideProgressLoading()
                },
                onSuccess = { s ->
                    viewState.apply {
                        hideProgressLoading()
                        setAccounts(canShowMenu, loggedSessions)
                        setUnLoggedAccounts(canShowMenu, unLoggedSessions)
                        setLoginToAnotherAccountButton()
                    }
                })
    }


    override fun logoutFromAccount(session: UserSessionModel) {
        compositeDisposable += userRepository.deleteUsersDeviceSession(session.sessionId)
            .andThen(userRepository.getAllUsersSessionsFromCurrentDevice(mDeviceId))
            .doOnSuccess { s ->
                transformData(s.userSessions)
            }
            .doFinally {
                if (isCurrentUser(session.userId.toString())) {
                    canShowMenu = false
                    userRepository.logout(appData.getId())
                        .subscribeSimple {
                            clearAppData()
                        }
                }
            }
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    viewState.showRequestErrorMessage()
                    it.printStackTrace()
                },
                onSuccess = {
                    viewState.apply {
                        setAccounts(canShowMenu, loggedSessions)
                        setUnLoggedAccounts(canShowMenu, unLoggedSessions)
                        if (isCurrentUser(session.userId.toString())) {
                            disableBackClick()
                        }
                    }
                })
    }

    fun logoutFromAccountAndKill(session: UserSessionModel) {
        compositeDisposable += userRepository.deleteUsersDeviceSession(session.sessionId)
            .andThen(userRepository.killUsersDeviceSession(session.sessionId))
            .doOnComplete {
                loggedSessions.remove(session)
                if (isCurrentUser(session.userId.toString())) {
                    canShowMenu = false
                    clearAppData()
                }
            }
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                    viewState.showRequestErrorMessage()
                }, onComplete = {
                    viewState.apply {
                        setAccounts(canShowMenu, loggedSessions)
                        setUnLoggedAccounts(canShowMenu, unLoggedSessions)
                        if (isCurrentUser(session.userId.toString())) {
                            disableBackClick()
                        }
                    }
                })
    }

    fun killSession(session: UserSessionModel) {
        compositeDisposable += userRepository.killUsersDeviceSession(session.sessionId)
            .doOnComplete {
                unLoggedSessions.remove(session)
            }
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                    viewState.showRequestErrorMessage()
                }, onComplete = {
                    viewState.apply {
                        setAccounts(canShowMenu, loggedSessions)
                        setUnLoggedAccounts(canShowMenu, unLoggedSessions)
                    }
                })
    }

    override fun switchAccount(session: UserSessionModel) {
        viewState.showCustomProgressDialog()
        if (!isCurrentUser(session.binds.user.id.toString())) {
            compositeDisposable += Completable.fromAction {
                appData.login(session.sessionUid)
                appData.saveId(session.userId)
                appData.setAllUserInfo(session.binds.user)
            }.performOnBackgroundOutOnMain()
                .subscribeSimple {
                    val m = "Аккаунт сменен"
                    viewState.showMessage(m)
                }
        } else {
            val m = "Вы уже авторизованы в данном аккаунте"
            viewState.apply {
                showMessage(m)
                hideCustomProgressDialog()
            }
        }
    }


    private fun logoutFromAccount() {
        compositeDisposable += userRepository.logout(appData.getId())
            .doOnComplete {
                appData.isSubscribedToPush = false
                socket.disconnectFromSocket()
                appData.logout()
                notificationManager.cancelAll()
            }
            .performOnBackgroundOutOnMain()
            .subscribeBy(
                onError = {
                    it.printStackTrace()
                    viewState.showRequestErrorMessage()
                }
            )
    }

    override fun authToAccountClick() {
        viewState.apply {
            showAuthorizationFragment()
            enableBackClick()
        }
    }

    override fun loginToAccountClick(user: UserDetail) {
        var login = ""
        if (user.email != null) {
            if (!user.email?.value.isNullOrEmpty() && user.email?.isConfirmed == true) {
                login = user.email?.value ?: ""
            }
        } else {
            user.phone?.forEach {
                if (it.type == "personal") {
                    login = it.value ?: ""
                }
            }
        }
        viewState.apply {
            showLoginFragment(login)
            enableBackClick()
        }
    }

    fun getUserId(): String = currentUserId
    fun isCurrentUser(id: String): Boolean {
        return id == currentUserId
    }

    private fun transformData(sessions: List<UserSessionModel>) {
        loggedSessions.clear()
        unLoggedSessions.clear()
        sessions.sortedBy { x -> !isCurrentUser(x.userId.toString()) }.forEach { session ->
            if (session.isLogged) {
                loggedSessions.add(session)
            } else {
                unLoggedSessions.add(session)
            }
        }
    }

    private fun clearAppData() {
        appData.isSubscribedToPush = false
        socket.disconnectFromSocket()
        appData.logoutNew()
        notificationManager.cancelAll()
    }
}