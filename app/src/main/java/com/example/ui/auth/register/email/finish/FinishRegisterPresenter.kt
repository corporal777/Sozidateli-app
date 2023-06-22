package com.example.ui.auth.register.email.finish

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.bodies.ConfirmCodeBody
import com.example.data.bodies.EmailCodeBody
import com.example.data.models.FieldDetails
import com.example.data.models.SnUser
import com.example.data.models.UserDetail
import com.example.data.socket.SocketConnectionState
import com.example.data.socket.SocketIOManager
import com.example.events.OnSocketConnectEvent
import com.example.exceptions.CodeInvalidException
import com.example.repository.AuthRepository
import com.example.repository.ChatRepository
import com.example.repository.UserRepository
import com.example.ui.auth.base.BaseAuthPresenter
import com.example.ui.snAuth.SnAuthManager
import com.example.ui.views.AddPhoneEmailDialog
import com.example.util.*
import com.example.util.Utils.validatePhoneBeforeSend
import com.google.gson.Gson
import com.shakebugs.shake.Shake
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import org.greenrobot.eventbus.EventBus
import performOnBackgroundOutOnMain
import retrofit2.HttpException
import withCustomProgressBarLoadingDialog
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class FinishRegisterPresenter
@Inject constructor(
    private val appData: AppData,
    private val authRepository: AuthRepository,
    private val phoneNumberUtil: PhoneNumberUtil,
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository,
    private val socket: SocketIOManager,
    snAuthManager: SnAuthManager
) : BaseAuthPresenter<FinishRegisterContract.View>(authRepository, snAuthManager, appData),
    FinishRegisterContract.Presenter {

    var firstName: String = ""
    var lastName: String = ""
    var middleName: String? = ""
    var login = ""
    var noMiddleNameChecked = false
    private var code = ""
    var loginType = "email"
    private var deviceId = appData.deviceId
    private var deviceModel = getDeviceName()
    private var appVersion = getAppVersion()
    private var appCode = getAppVersionCode()

    private val timerCompositeDisposable = CompositeDisposable()

    override fun attachView(view: FinishRegisterContract.View?) {
        super.attachView(view)
    }


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += timerCompositeDisposable
        viewState.apply {
            setData(login, firstName, lastName, middleName)
            setDescriptionText(loginType, login)
            if (loginType == "email") enableRegisterBtn(code.length == 6)
            else enableRegisterBtn(code.length == 4)
        }
        startTimer()
    }

    override fun checkEmailPhoneUnique() {
        loginType = if (Utils.isPhone(login) && !Utils.isContainLetters(login)) "phone"
        else "email"
        if (checkEmailValid()) {
            compositeDisposable += Completable.defer {
                if (loginType == "email") userRepository.checkEmailPhone(login, null)
                else userRepository.checkEmailPhone(null, validatePhoneBeforeSend(login))
            }
                .performOnBackgroundOutOnMain()
                .withCustomProgressBarLoadingDialog(viewState)
                .subscribeSimple(
                    onError = {
                        it.printStackTrace()
                        viewState.showEmailPhoneNotUnique(login, loginType)
                    },
                    onComplete = {
                        sendCodeAgain()
                    })
        }
    }


    private fun startTimer() {
        timerCompositeDisposable.clear()
        viewState.apply {
            setCanResend(false)
            setTimeLeft(FinishRegisterPresenter.TIMER_SECONDS_COUNT)
            showHideDescriptionText(true)
        }

        timerCompositeDisposable += Observable.interval(1000, TimeUnit.MILLISECONDS)
            .performOnBackgroundOutOnMain()
            .subscribe({
                val timeLeft = FinishRegisterPresenter.TIMER_SECONDS_COUNT - (it.toInt() + 1)
                if (timeLeft < 0) {
                    timerCompositeDisposable.clear()
                    viewState.setCanResend(true)
                } else {
                    viewState.setTimeLeft(timeLeft)
                }
            }, {
                it.printStackTrace()
            })
    }


    override fun sendCodeAgain() {
        compositeDisposable += Completable.defer {
            if (loginType == "email") authRepository.registerEmailResend(login)
            else authRepository.registerPhoneResend("personal", validatePhoneBeforeSend(login))
        }
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = { it.printStackTrace() },
                onComplete = {
                    viewState.setDescriptionText(loginType, login)
                    startTimer()
                }
            )

    }

    override fun onHandleAuthLink() {
        viewState.setIgnoreTokenListener(true)
        compositeDisposable += Completable.create { emitter ->
            val disposable = CompositeDisposable()
            disposable += userRepository.updateProfile(appData.getId(), getUpdateRequestBody())
                .subscribeSimple(
                    onError = { emitter.onError(it) },
                    onSuccess = { user ->
                        disposable += confirmCodeRequest().subscribeSimple(
                            onError = { emitter.onError(CodeInvalidException()) },
                            onComplete = {
                                Shake.registerUser(user.id.toString())
                                updateUserInShake(user)
                                emitter.onComplete()
                            })
                    })
            emitter.setDisposable(disposable)
        }
            .doOnComplete { viewState.connectToSocket() }
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    if (it is CodeInvalidException) viewState.codeError()
                    else onReceiveError(it)
                },
                onComplete = {
                    viewState.apply {
                        setIgnoreTokenListener(false)
                        openHome()
                    }
                }
            )
    }

    override fun onChangeCodeText(code: String) {
        this.code = code
        if (loginType == "email") viewState.enableRegisterBtn(code.length == 6)
        else viewState.enableRegisterBtn(code.length == 4)
    }

    override fun onChangeNameText(name: String) {
        this.firstName = name
    }

    override fun onChangeLastNameText(lastName: String) {
        this.lastName = lastName
    }

    override fun onChangeMiddleNameText(lastName: String) {
        this.middleName = lastName
    }

    override fun onChangeEmailText(email: String) {
        this.login = email
        loginType = if (Utils.isPhone(login) && !Utils.isContainLetters(login)) "phone" else "email"
        viewState.apply {
            showHideDescriptionText(false)
            setCanResend(true)
        }
    }

    override fun onNoMiddleNameChecked(checked: Boolean) {
        noMiddleNameChecked = checked
        viewState.enableMiddleNameInput(!checked)
    }


    override fun onCloseClick() {
        viewState.setIgnoreTokenListener(true)
        compositeDisposable += Completable.fromAction {
            appData.isSubscribedToPush = false
            appData.logout()
        }
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                viewState.apply {
                    setIgnoreTokenListener(false)
                    logout()
                }
            }
    }

    override fun onContinueWithSnRegistration(snUser: SnUser) {
    }

    private fun checkEmailValid(): Boolean {
        var isValid = true
        if (loginType == "email") {
            if (!AuthValidateUtil.isValidEmail(login)) {
                isValid = false
                viewState.showWrongEmailError(true)
            }
        } else {
            if (!Utils.isNewPhoneIsValid(validatePhoneBeforeSend(login))) {
                viewState.showWrongPhoneError(true)
                isValid = false
            }
        }
        return isValid
    }

    fun isConfirmCodeValid(codeLength: Int): Boolean {
        return if (loginType == "email") codeLength == AddPhoneEmailDialog.EMAIL_CODE_SIZE
        else codeLength == AddPhoneEmailDialog.PHONE_CODE_SIZE
    }

    private fun confirmCodeRequest(): Completable {
        return if (loginType == "email") authRepository.confirmEmailCode(
            EmailCodeBody(
                code = code,
                email = login
            )
        )
        else authRepository.confirmPhoneCode(
            ConfirmCodeBody(
                "personal",
                validatePhoneBeforeSend(login),
                code
            )
        )
    }

    private fun getUpdateRequestBody(): MutableMap<String, Any> {
        return mutableMapOf<String, Any>().apply {
            if (loginType == "email") {
                put(UserDetail.USER_EMAIL, FieldDetails(value = login, isVisible = true))
            } else {
                put(
                    UserDetail.USER_PHONE,
                    FieldDetails(
                        value = validatePhoneBeforeSend(login),
                        type = PHONE_PERSONAL,
                        isVisible = true
                    ).toList()
                )
            }
            put(UserDetail.USER_NAME, firstName)
            put(UserDetail.USER_LAST_NAME, lastName)
            put(
                UserDetail.USER_MIDDLE_NAME,
                FieldDetails(value = middleName, absent = noMiddleNameChecked)
            )
            put(UserDetail.USER_REGISTRATION_FINISH, true)
        }
    }


    companion object {
        val TIMER_SECONDS_COUNT = 60
    }
}
