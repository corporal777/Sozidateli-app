package com.example.ui.status

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.asOptional
import com.example.data.models.user.User
import com.example.ui.base.BasePresenter
import io.reactivex.Completable
import io.reactivex.rxkotlin.plusAssign
import performOnBackground
import performOnBackgroundOutOnMain
import withLoadingDialog
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class StatusPresenter
@Inject constructor(
        private val appData: AppData
) : BasePresenter<StatusContract.View>(), StatusContract.Presenter {

    lateinit var status: User.Status

    private var phone = appData.getUser().user_phone

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += appData.userChangeSubject
                .performOnBackground()
                .subscribe({
                    viewState.cancelVerification()
                    val user = it.value
                    phone = user?.user_phone
                    viewState.setStatus(status, status == user?.user_status, phone, false)
                }, {
                    it.printStackTrace()
                })
    }

    override fun onAddPhoneClick() {
        viewState.checkPassword(VERIFICATION_ACTION_ADD)
    }

    override fun onChangePhoneClick() {
        viewState.checkPassword(VERIFICATION_ACTION_CHANGE)
    }

    override fun onRemovePhoneClick() {
        viewState.checkPassword(VERIFICATION_ACTION_REMOVE)
    }

    override fun onPasswordInputComplete(password: String, action: Int) {
        dummyCall {
            when (action) {
                VERIFICATION_ACTION_REMOVE -> viewState.showRemovePhone(phone ?: "")
                else -> viewState.showChangePhone(action)
            }
        }
    }

    override fun onPhoneInputComplete(phone: String, action: Int, saveFlag: Int) {
        dummyCall { viewState.showCode(phone, action, saveFlag) }
    }

    override fun onCodeInputComplete(phone: String, code: String, saveFlag: Int) {
        dummyCall {
            // for tests
            appData.getUser().apply {
                user_status = User.Status.MID_PROTECTION
                user_phone = phone
            }
            appData.userChangeSubject.onNext(appData.getUser().asOptional())
        }
    }

    override fun onDoNotReceiveCodeClick(phone: String) {
        dummyCall { viewState.showSentNewCodeMessage(phone) }
    }

    override fun onPhoneRemoveAccept() {
        dummyCall {
            // for tests
            appData.getUser().apply {
                user_status = User.Status.LOW_PROTECTION
                user_phone = null
            }
            appData.userChangeSubject.onNext(appData.getUser().asOptional())
        }
    }

    private fun dummyCall(onComplete: () -> Unit) {
        compositeDisposable += Completable.complete()
                .delay(1, TimeUnit.SECONDS)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe {
                    viewState.showToast("ОЖИДАЕТ РЕАЛИЗАЦИИ")
                    onComplete()
                }
    }

    companion object {
        const val VERIFICATION_ACTION_ADD = 0
        const val VERIFICATION_ACTION_CHANGE = 1
        const val VERIFICATION_ACTION_REMOVE = 2

        const val VERIFICATION_ADD_PHONE_NONE = 0
        const val VERIFICATION_ADD_PHONE_MOBILE = 1
        const val VERIFICATION_ADD_PHONE_WORK = 2

        const val VERIFICATION_ERROR_WRONG_PASSWORD = 1
        const val VERIFICATION_ERROR_WRONG_CODE = 2
    }
}
