package com.example.ui.userprofile.read.settings.change_phone

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.FieldDetails
import com.example.data.models.UserDetail
import com.example.data.models.UserDetail.Companion.USER_PHONE
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.base.bottomSheet.BaseBottomSheetPresenter
import com.example.util.Utils
import com.example.util.phoneToServer
import io.reactivex.Single
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCustomProgressBarLoadingDialog
import javax.inject.Inject

@InjectViewState
class ChangePhonePresenter
@Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val appData: AppData,
) : BaseBottomSheetPresenter<ChangePhoneContract.View>(appData),
    ChangePhoneContract.Presenter {

    var mobilePhone: String = ""
    var oldMobilePhone: String = ""
    var isConfirmed = false
    var isVisible = false
    var phoneField: FieldDetails? = null
    var withUpdate = false


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.apply {
            setUserPhone(mobilePhone)
            setUserPhoneIsConfirmed(isConfirmed)
            setUserPhoneIsVisible(isVisible)
        }
    }

    override fun setNewPhone(phone: String) {
        this.mobilePhone = phone
    }

    override fun setNewPhoneIsConfirmed(phone: String) {
        if (phoneField?.isConfirmed == true) {
            this.isConfirmed = phone.phoneToServer() == oldMobilePhone
            viewState.setUserPhoneIsConfirmed(isConfirmed)
        }
    }

    override fun setNewPhoneIsVisible(isVisible: Boolean) {
        this.isVisible = isVisible
    }

    override fun onSaveNewPhoneClick(phone: String) {
        if (phoneField?.isConfirmed == true) {
            if (this.isConfirmed) {
                updatePhoneData()
            } else {
                viewState.showEnterPassword(phone)
            }
        } else {
            updatePhoneData()
        }
    }

    override fun checkPassword(password: String, phone: String) {
        compositeDisposable += userRepository.checkPasswordNew(password)
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    viewState.hideEnterPassword()
                }, onComplete = {
                    viewState.hideEnterPassword()
                    checkPhoneIsUnique(phone)
                })
    }

    override fun checkPhoneIsUnique(phone: String) {
        compositeDisposable += userRepository.checkEmailPhone(null, phone)
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    viewState.showPhoneNotUnique(phone)
                },
                onComplete = {
                    onShowPhoneConfirm(phone)
                })
    }

    override fun onShowPhoneConfirm(phone: String) {
        compositeDisposable += authRepository.registerPhoneResend("personal", phone)
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple {
                viewState.showPhoneConfirmation(phone)
            }
    }


    override fun updatePhoneData() {
        compositeDisposable += updatePhoneRequest()
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    onReceiveError(it)
                },
                onSuccess = {
                    viewState.apply {
                        showPhoneIsUpdatedSuccessfully()
                    }
                })
    }

    private fun getDataToSave(): Map<String, Any?> {
        return mapOf(
            USER_PHONE to arrayListOf(
                FieldDetails(
                    value = Utils.validatePhoneBeforeSend(mobilePhone.phoneToServer()?:""),
                    type = phoneField?.type,
                    isVisible = isVisible,
                    isConfirmed = isConfirmed,
                )
            )
        )
    }

    private fun updatePhoneRequest(): Single<UserDetail> {
        return userRepository.updateUserProfile(
            appData.getId(),
            getDataToSave()
        )
    }

    fun isWithUpdate(): Boolean = withUpdate


}