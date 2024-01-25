package com.example.ui.userprofile.edit.phone

import android.util.Log
import com.example.data.AppData
import com.example.data.bodies.FieldPhoneBody
import com.example.data.models.FieldDetails
import com.example.data.models.UserDetail.Companion.USER_PHONE
import com.example.exceptions.PhoneNotUniqueException
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.PHONE_PERSONAL
import com.example.util.Utils
import com.example.util.Utils.isPhoneNumberValid
import io.reactivex.Completable
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import withCustomLoading
import javax.inject.Inject

@InjectViewState
class ChangePhonePresenter
@Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val appData: AppData,
) : BasePresenter<ChangePhoneContract.View>(appData), ChangePhoneContract.Presenter {

    private var phoneField: FieldDetails? = null

    private var phone: String = ""
    private var phoneIsVisible: Boolean = true
    private var phoneIsConfirmed: Boolean = false


    override fun attachView(view: ChangePhoneContract.View?) {
        super.attachView(view)
        phoneField = appData.getUser().phone?.firstOrNull { it.type == PHONE_PERSONAL }
        phone = phoneField?.value ?: ""
        phoneIsVisible = phoneField?.isVisible ?: true
        phoneIsConfirmed = phoneField?.isConfirmed ?: false

        viewState.setUserPhone(phone, phoneIsVisible)
        performDataChange()
    }


    override fun onSavePhoneClick(withCheck: Boolean) {
        compositeDisposable += Completable.defer {
            if (withCheck && !isSamePhone())
                userRepository.checkEmailPhone(null, phone)
                    .onErrorResumeNext { Completable.error(PhoneNotUniqueException()) }
            else Completable.complete()
        }
            .andThen(updatePhoneRequest())
            .performOnBackgroundOutOnMain()
            .withCustomLoading(viewState)
            .subscribeSimple(
                onError = {
                    if (it is PhoneNotUniqueException) viewState.showPhoneNotUnique(phone)
                    else onReceiveError(it)
                },
                onComplete = {
                    viewState.navigateUp()
                    //if (phoneIsConfirmed && !isSamePhone()) onShowConfirmClick(false)
                    //else viewState.navigateUp()
                })
    }

    override fun onShowConfirmClick(withAdd: Boolean) {
        viewState.showPhoneConfirmation(phone, withAdd)
    }

    override fun onChangePhone(phone: String) {
        this.phone = phone
        performDataChange()
    }

    override fun onChangePhoneVisible(isVisible: Boolean) {
        this.phoneIsVisible = isVisible
    }

    private fun updatePhoneRequest(): Completable {
        return userRepository.updateUserProfileField(
            mapOf(
                USER_PHONE to FieldPhoneBody(
                    value = phone,
                    type = phoneField?.type,
                    isVisible = phoneIsVisible
                ).toList()
            )
        ).doOnSuccess { new -> appData.updateUser { this.phone = new.phone } }.ignoreElement()
    }

    private fun isSamePhone() = phoneField?.value == phone

    private fun performDataChange() = viewState.apply {
        enableBtnSave(isPhoneNumberValid(phone))
        enableBtnConfirm(
            if (isSamePhone()) phoneIsConfirmed else false,
            isPhoneNumberValid(phone)
        )
    }

}