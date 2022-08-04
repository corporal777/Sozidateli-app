package com.example.ui.accountChange

import android.app.NotificationManager
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
    private var usersList: ArrayList<UserDetail> = arrayListOf()
    private val loggedSessionsMap = mutableMapOf<UserSessionModel, UserDetail>()
    private val unLoggedSessionsMap = mutableMapOf<UserSessionModel, UserDetail>()
    private var canShowMenu = true
    private var currentUserId = appData.getId().toString()

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
        viewState.setAppBarElevation(0f)
        viewState.showProgressLoading()
        loadData()
    }

    private fun loadData() {
        val userIds = StringBuilder()
        compositeDisposable += userRepository.getAllUsersSessionsFromCurrentDevice(mDeviceId)
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                    viewState.hideProgressLoading()
                },
                onSuccess = { s ->
                    s.userSessions.forEach { session -> userIds.append("," + session.userId) }
                    userIds.deleteCharAt(0)
                    compositeDisposable += userRepository.getUsersWithoutPagination(mapOf(UserDetail.USER_ID to userIds.toString()))
                        .doOnSuccess { list ->
                            usersList.addAll(list)
                            transformData(list, s.userSessions)
                        }
                        .performOnBackgroundOutOnMain()
                        .subscribeSimple(
                            onError = {
                                it.printStackTrace()
                                viewState.hideProgressLoading()
                            },
                            onSuccess = {
                                viewState.apply {
                                    hideProgressLoading()
                                    setAccounts(canShowMenu, loggedSessionsMap)
                                    setUnLoggedAccounts(canShowMenu, unLoggedSessionsMap)
                                    setLoginToAnotherAccountButton()
                                }
                            })
                })
    }


    override fun logoutFromAccount(session: UserSessionModel, userDetail: UserDetail) {
        compositeDisposable += userRepository.deleteUsersDeviceSession(session.sessionId)
            .andThen(userRepository.getAllUsersSessionsFromCurrentDevice(mDeviceId))
            .doOnSuccess { s ->
                transformData(usersList, s.userSessions)
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
            //.withProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    viewState.showRequestErrorMessage()
                    it.printStackTrace()
                },
                onSuccess = {
                    viewState.apply {
                        setAccounts(canShowMenu, loggedSessionsMap)
                        setUnLoggedAccounts(canShowMenu, unLoggedSessionsMap)
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
                loggedSessionsMap.remove(session)
                if (isCurrentUser(session.userId.toString())) {
                    canShowMenu = false
                    clearAppData()
                }
            }
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            //.withProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                    viewState.showRequestErrorMessage()
                }, onComplete = {
                    viewState.apply {
                        setAccounts(canShowMenu, loggedSessionsMap)
                        setUnLoggedAccounts(canShowMenu, unLoggedSessionsMap)
                        if (isCurrentUser(session.userId.toString())) {
                            disableBackClick()
                        }
                    }
                })
    }

    fun killSession(session: UserSessionModel) {
        compositeDisposable += userRepository.killUsersDeviceSession(session.sessionId)
            .doOnComplete {
                unLoggedSessionsMap.remove(session)
            }
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            //.withProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                    viewState.showRequestErrorMessage()
                }, onComplete = {
                    viewState.apply {
                        setAccounts(canShowMenu, loggedSessionsMap)
                        setUnLoggedAccounts(canShowMenu, unLoggedSessionsMap)
                    }
                })
    }

    override fun switchAccount(session: UserSessionModel, userDetail: UserDetail) {
        viewState.showCustomProgressDialog()
        if (!isCurrentUser(userDetail.id.toString())) {
            compositeDisposable += Completable.fromAction {
                appData.login(session.sessionUid)
                appData.saveId(session.userId)
                appData.setAllUserInfo(userDetail)
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

    fun getUserId(): String = appData.getId().toString()
    fun isCurrentUser(id: String): Boolean {
        return id == currentUserId
    }

    private fun transformData(list: List<UserDetail?>, sessions: List<UserSessionModel>) {
        loggedSessionsMap.clear()
        unLoggedSessionsMap.clear()
        sessions.sortedBy { x -> !isCurrentUser(x.userId.toString()) }
            .forEach { session ->
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

    private fun clearAppData() {
        appData.isSubscribedToPush = false
        socket.disconnectFromSocket()
        appData.logoutNew()
        notificationManager.cancelAll()
    }
}