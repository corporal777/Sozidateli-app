package com.example.ui.userprofile.edit.contacts

import com.example.data.AppData
import com.example.data.models.UserEditDataType
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.ui.userprofile.edit.education.EditEducationContract
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import withCustomLoading
import withInfinityCustomLoading
import javax.inject.Inject

@InjectViewState
class EditContactsPresenter
@Inject constructor(
    private val appData: AppData,
    private val userRepository: UserRepository
) : BasePresenter<EditContactsContract.View>(appData), EditContactsContract.Presenter {

    private var isUpdating = false

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setPlaceholder()
        compositeDisposable += appData.userChangeSubject
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { viewState.navigateUp() },
                onNext = {
                    val user = it.value
                    if (user == null) viewState.navigateUp()
                    else if (isUpdating) isUpdating = false
                    else viewState.setContactsData(user)
                })
    }

    override fun checkPhoneIsUnique(phone: String, withUpdate: Boolean) {
        isUpdating = true
        compositeDisposable += userRepository.checkEmailPhone(null, phone)
            .performOnBackgroundOutOnMain()
            .withCustomLoading(viewState)
            .subscribeSimple(
                onError = { viewState.showPhoneNotUnique(phone, withUpdate) },
                onComplete = { viewState.showPhoneConfirmation(phone, withUpdate) }
            )
    }

    override fun onSaveContactsClick(data: MutableMap<String, Any?>) {
        isUpdating = true
        compositeDisposable += userRepository.updateUserProfile(appData.getId(), data)
            .flatMap { userRepository.checkUserProfileSingle() }
            .performOnBackgroundOutOnMain()
            .withCustomLoading(viewState)
            .subscribeSimple(
                onError = { it.printStackTrace() },
                onSuccess = { viewState.navigateUp() })
    }

    override fun onUpdatePhone() {
        val phone = getUserData().personalPhone
        viewState.updatePhone(phone)
    }

    override fun onChangeEmailClick() = viewState.showChangeEmail()

    fun getBaseUserState() = appData.hasBaseState
    fun getMaxUserState() = appData.hasMaxState
}