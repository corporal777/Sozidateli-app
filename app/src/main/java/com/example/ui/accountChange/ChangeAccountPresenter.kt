package com.example.ui.accountChange

import android.app.NotificationManager
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.bodies.AuthBody
import com.example.data.bodies.LoginModel
import com.example.data.bodies.RebaseInviteBody
import com.example.data.models.ApiError
import com.example.data.models.UserDetail
import com.example.data.models.UserSessionModel
import com.example.data.models.asOptional
import com.example.data.socket.SocketIOManager
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.auth.login.LoginPresenter
import com.example.ui.base.BasePresenter
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.zipWith
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withProgressBarLoadingDialog
import javax.inject.Inject

@InjectViewState
class ChangeAccountPresenter
@Inject constructor(
    private val userRepository: UserRepository,
    private val appData: AppData,
    private val socket: SocketIOManager,
    private val notificationManager: NotificationManager,
    private val authRepository: AuthRepository
) : BasePresenter<ChangeAccountContract.View>(appData), ChangeAccountContract.Presenter {

    var mDeviceId = ""

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setAppBarElevation(0f)
        loadData()
    }

    private fun loadData() {
        val userIds = StringBuilder()
        compositeDisposable += userRepository.getAllUsersSessionsFromCurrentDevice(mDeviceId)
            .doOnSuccess {
                it.userSessions.forEach { session ->
                    userIds.append("," + session.userId)
                }
                userIds.deleteCharAt(0)
            }
            .subscribeSimple { s ->
                val loggedSessionsMap = mutableMapOf<UserSessionModel, UserDetail>()
                val unLoggedSessionsMap = mutableMapOf<UserSessionModel, UserDetail>()
                compositeDisposable += userRepository.getUsersWithoutPagination(mapOf(UserDetail.USER_ID to userIds.toString()))
                    .doOnSuccess { list ->
                        s.userSessions.forEach { session ->
                            list.forEach { user ->
                                if (session.userId == user?.id) {
                                    if (session.isLogged) {
                                        loggedSessionsMap.put(session, user)
                                    } else {
                                        unLoggedSessionsMap.put(session, user)
                                    }
                                }
                            }
                        }
                    }
                    .performOnBackgroundOutOnMain()
                    .withProgressBarLoadingDialog(viewState)
                    .subscribeSimple { users ->
                        val sortedMap = loggedSessionsMap.toSortedMap(compareBy
                        { !isCurrentUser(it.userId.toString()) })
                        viewState.setAccounts(sortedMap)
                        if (!unLoggedSessionsMap.isNullOrEmpty()) {
                            viewState.setUnLoggedAccounts(unLoggedSessionsMap)
                        }
                    }
            }
    }

    override fun changeAppBarElevation(value: Int) {

    }

    override fun logoutFromAccount(session: UserSessionModel) {
        compositeDisposable += userRepository.deleteUsersDeviceSession(session.sessionId)
            .performOnBackgroundOutOnMain()
            .withProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                }, onComplete = {
                    if (session.userId == appData.getId()) {
                        logoutFromAccount()
                    } else {
                        loadData()
                    }
                })
    }

    fun logoutFromAccountAndKill(session: UserSessionModel) {
        compositeDisposable += userRepository.deleteUsersDeviceSession(session.sessionId)
            .andThen(userRepository.logout(appData.getId()))
            .doOnComplete {
                appData.isSubscribedToPush = false
                socket.disconnectFromSocket()
                appData.logout()
                notificationManager.cancelAll()
            }
            .andThen(userRepository.killUsersDeviceSession(session.sessionId))
            .performOnBackgroundOutOnMain()
            .withProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                }, onComplete = {

                })
    }

    fun killSession(session: UserSessionModel) {
        compositeDisposable += userRepository.killUsersDeviceSession(session.sessionId)
            .performOnBackgroundOutOnMain()
            .withProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                }, onComplete = {
                    loadData()
                })
    }

    override fun switchAccount(session: UserSessionModel, userDetail: UserDetail) {
        if (!isCurrentUser(userDetail.id.toString())) {
            compositeDisposable += Completable.fromAction {
                appData.login(session.sessionUid)
                appData.saveId(session.userId)
                appData.setAllUserInfo(userDetail)
            }.performOnBackgroundOutOnMain()
                .withProgressBarLoadingDialog(viewState)
                .subscribeSimple {
                    val m = "Аккаунт сменен"
                    viewState.showMessage(m)
                }
        } else {
            val m = "Вы уже авторизованы в данном аккаунте"
            viewState.showMessage(m)
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
        viewState.showAuthorizationFragment()
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
        viewState.showLoginFragment(login)
    }

    fun getUserId(): String = appData.getId().toString()
    fun isCurrentUser(id: String): Boolean {
        return id == appData.getId().toString()
    }

}