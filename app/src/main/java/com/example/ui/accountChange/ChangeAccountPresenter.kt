package com.example.ui.accountChange

import android.app.NotificationManager
import android.net.Uri
import com.example.data.AppData
import com.example.data.models.UserDetail
import com.example.data.models.UserSessionModel
import com.example.data.socket.SocketIOManager
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import io.reactivex.Completable
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import withProgressBarDialogLoading
import javax.inject.Inject

@InjectViewState
class ChangeAccountPresenter
@Inject constructor(
    private val userRepository: UserRepository,
    private val appData: AppData,
    private val socket: SocketIOManager,
    private val notificationManager: NotificationManager
) : BasePresenter<ChangeAccountContract.View>(appData), ChangeAccountContract.Presenter {

    private val mDeviceId = appData.deviceId ?: ""
    private val loggedSessions = arrayListOf<UserSessionModel>()
    private val unLoggedSessions = arrayListOf<UserSessionModel>()
    private var canShowMenu = true
    private val currentUserId = appData.getId().toString()

    var redirectLink : String? = ""

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setAccounts(false, List(4) { null })
        compositeDisposable += userRepository.getAllUsersSessionsFromCurrentDevice(mDeviceId)
            .doOnSuccess { transformData(it.userSessions.filter { x -> x.deviceId == mDeviceId }) }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = {
                    viewState.apply {
                        setAccounts(canShowMenu, loggedSessions)
                        setUnLoggedAccounts(canShowMenu, unLoggedSessions)
                        setLoginToAnotherAccountButton()
                    }
                })
    }


    override fun logoutFromAccount(session: UserSessionModel) {
        compositeDisposable += userRepository.deleteUsersDeviceSession(session.sessionId.toInt())
            .andThen(userRepository.getAllUsersSessionsFromCurrentDevice(mDeviceId))
            .doOnSuccess { s -> transformData(s.userSessions) }
            .flatMapCompletable { clearAppData(session) }
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onComplete = {
                    viewState.apply {
                        setAccounts(canShowMenu, loggedSessions)
                        setUnLoggedAccounts(canShowMenu, unLoggedSessions)
                    }
                })
    }

    fun logoutFromAccountAndKill(session: UserSessionModel) {
        compositeDisposable += userRepository.deleteUsersDeviceSession(session.sessionId.toInt())
            .andThen(userRepository.killUsersDeviceSession(session.sessionId.toInt()))
            .doOnComplete { loggedSessions.remove(session) }
            .andThen(clearAppData(session))
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onComplete = {
                    viewState.apply {
                        setAccounts(canShowMenu, loggedSessions)
                        setUnLoggedAccounts(canShowMenu, unLoggedSessions)
                    }
                })
    }

    fun killSession(session: UserSessionModel) {
        compositeDisposable += userRepository.killUsersDeviceSession(session.sessionId.toInt())
            .doOnComplete { unLoggedSessions.remove(session) }
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onComplete = {
                    viewState.apply {
                        setAccounts(canShowMenu, loggedSessions)
                        setUnLoggedAccounts(canShowMenu, unLoggedSessions)
                    }
                })
    }

    override fun switchAccount(session: UserSessionModel) {
        if (!redirectLink.isNullOrEmpty()) observeDeeplink(session)
        else {
            viewState.showCustomProgressDialog()
            if (!isCurrentUser(session.binds.user.id.toString())) {
                compositeDisposable += Completable.fromAction {
                    viewState.setIgnoreTokenListener(false)
                    appData.login(session.sessionUid)
                    appData.saveId(session.userId)
                    appData.setAllUserInfo(session.binds.user)
                }.performOnBackgroundOutOnMain()
                    .subscribeSimple {
                        viewState.showMessage("Аккаунт сменен")
                    }
            } else {
                viewState.apply {
                    showMessage("Вы уже авторизованы в данном аккаунте")
                    hideCustomProgressDialog()
                }
            }
        }
    }


    override fun authToAccountClick() {
        viewState.apply {
            setIgnoreTokenListener(false)
            showAuthorizationFragment()
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
                if (it.type == "personal") login = it.value ?: ""
            }
        }
        viewState.apply {
            if (!login.isNullOrEmpty()){
                setIgnoreTokenListener(false)
                showLoginFragment(login)
            }
        }
    }

    override fun onClickClose() {
        if (!appData.isLoggedOut) viewState.navigateUp()
    }

    fun getUserId(): String = currentUserId
    fun isCurrentUser(id: String) = id == currentUserId

    private fun transformData(sessions: List<UserSessionModel>) {
        loggedSessions.clear()
        unLoggedSessions.clear()
        val sorted = sessions.sortedBy { x -> !isCurrentUser(x.userId.toString()) }
        loggedSessions.addAll(sorted.filter { x -> x.isLogged })
        unLoggedSessions.addAll(sorted.filter { x -> !x.isLogged })
    }

    private fun observeDeeplink(currentSession: UserSessionModel) {
        val uri = Uri.parse(redirectLink)
            .buildUpon()
            .appendQueryParameter("new_session", "true")
            .appendQueryParameter("access_token", currentSession.sessionUid)
            .build()
        viewState.showBrowser(uri.toString())
    }

    private fun clearAppData(session: UserSessionModel): Completable {
        return if (isCurrentUser(session.userId.toString())) {
            canShowMenu = false
            userRepository.logout(appData.getId())
                .doOnComplete {
                    viewState.setIgnoreTokenListener(true)
                    appData.isSubscribedToPush = false
                    socket.disconnectFromSocket()
                    appData.logout()
                    notificationManager.cancelAll()
                }
        } else Completable.complete()
    }
}