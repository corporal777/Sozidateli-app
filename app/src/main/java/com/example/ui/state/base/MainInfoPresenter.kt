package com.example.ui.state.base

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.FieldDetails
import com.example.data.models.UserDetail
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.ui.state.UserState
import com.example.util.AuthValidateUtil
import io.reactivex.Single
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class MainInfoPresenter
@Inject constructor(
        private val appData: AppData,
        private val userRepository: UserRepository
): BasePresenter<MainInfoContract.View>(), MainInfoContract.Presenter {

    lateinit var type: UserState
    var screen: Int = 1

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += appData.userNewChangeSubject
                .performOnBackgroundOutOnMain()
                .subscribe({
                    val user = it.value ?: throw RuntimeException("Edit null user")
                    viewState.apply {
                        compositeDisposable += userRepository.searchAddress(user.address?.getShortAddress()?: "")
                                .performOnBackgroundOutOnMain()
                                .subscribe({ add ->
                                    if (add.data?.isNotEmpty() == true)
                                        user.address?.shortAddres = add.data[0].region
                                    setPersonalData(user)
                                }, {
                                    setPersonalData(user)
                                })
                    }
                }, {
                    it.printStackTrace()
                    viewState.navigateUp()
                })
    }

    override fun onClickClose() {
        viewState.navigateUp()
    }

    fun getUserData() = appData.getUserNew()

    override fun updateFiles(data: MutableMap<String, Any?>) {
        onEditSave(data) {
            appData.updateUserNew {
                name = it.name
                middleName = it.middleName
                lastName = it.lastName
                birthday = it.birthday
                gender = it.gender
                notes = it.notes
                address = it.address
                phone = it.phone
            }
            true
        }
    }

    private fun onEditSave(data: MutableMap<String, Any?>, onComplete: (UserDetail) -> Boolean) {
        if (data.isEmpty()) {
            viewState.navigateUp()
            return
        }

        updateUser(userRepository.updateUserProfile(appData.getId(), data), onComplete)
    }

    private fun updateUser(request: Single<UserDetail>, onComplete: (UserDetail) -> Boolean) {
        compositeDisposable += request
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    appData.getUserNew().apply {
                        phone = it.phone
                        name = it.name
                        lastName = it.lastName
                        middleName = it.middleName
                        birthday = it.birthday
                        gender = it.gender
                        address = it.address
                    }
                    if (onComplete(it))
                        compositeDisposable += userRepository.checkUserProfileSingle()
                                .performOnBackgroundOutOnMain()
                                .subscribe({
                                    viewState.goToNext()
                                },{
                                    viewState.goToNext()
                                })
                        //viewState.goToNext()
                }, {
                    it.printStackTrace()
                    viewState.showUpdateError(it.message)
                })
    }

    override fun onChangeEmailClick() {
        viewState.showChangeEmail()
    }

    override fun onChangeEmailConfirm(email: String) {
        if (AuthValidateUtil.isValidEmail(email)) {
            updateUser(userRepository.updateProfile(appData.getId(), mapOf(UserDetail.USER_EMAIL to FieldDetails(value = email)))) {
                it.email?.value = email
                viewState.showChangeEmailComplete(email)
                false
            }
        } else {
            viewState.showUpdateError()
        }
    }

    override fun onConfirmPhoneClick(phone: String) {
        viewState.showPhoneConfirm(phone)
    }
}