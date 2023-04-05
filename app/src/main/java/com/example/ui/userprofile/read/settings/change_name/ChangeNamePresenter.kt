package com.example.ui.userprofile.read.settings.change_name

import android.app.NotificationManager
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.FieldDetails
import com.example.data.models.UserDetail
import com.example.data.models.UserDetail.Companion.USER_LAST_NAME
import com.example.data.models.UserDetail.Companion.USER_NAME
import com.example.data.socket.SocketIOManager
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.base.bottomSheet.BaseBottomSheetPresenter
import com.example.ui.userprofile.read.settings.change_password.ChangePasswordContract
import com.example.util.AuthValidateUtil
import com.example.util.Utils
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCustomProgressBarLoadingDialog
import javax.inject.Inject

@InjectViewState
class ChangeNamePresenter
@Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val appData: AppData,
) : BaseBottomSheetPresenter<ChangeNameContract.View>(appData),
    ChangeNameContract.Presenter {

    lateinit var userDetail: UserDetail
    private var firstName = ""
    private var lastName = ""
    private var middleName = ""
    private var isMiddleNameAbsent = false

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        firstName = userDetail.name ?: ""
        lastName = userDetail.lastName ?: ""
        middleName = userDetail.middleName?.value ?: ""
        isMiddleNameAbsent = userDetail.middleName?.absent ?: middleName.isNullOrBlank() || middleName == "-"

        viewState.setUserName(firstName, lastName, middleName, isMiddleNameAbsent)
    }

    override fun onSaveNameClick() {
        if (isDataValid()) {
            compositeDisposable += userRepository.updateUserProfileField(
                mutableMapOf<String, Any?>().apply {
                    if (firstName != userDetail.name) put(USER_NAME, firstName)
                    if (lastName != userDetail.lastName) put(USER_LAST_NAME, lastName)
                    if (middleName != userDetail.middleName?.value && isMiddleNameAbsent != userDetail.middleName?.absent) {
                        put(
                            UserDetail.USER_MIDDLE_NAME,
                            FieldDetails(value = middleName, absent = isMiddleNameAbsent)
                        )
                    }
                }
            )
                .doOnSuccess { new ->
                    appData.updateUserNew {
                        this.name = new.name
                        this.lastName = new.lastName
                        this.middleName = new.middleName
                    }
                }
                .performOnBackgroundOutOnMain()
                .withCustomProgressBarLoadingDialog(viewState)
                .subscribeSimple(
                    onError = { onReceiveError(it) },
                    onSuccess = { viewState.hideBottomSheetDialog() }
                )
        } else showErrors()

    }

    override fun performChangeName(value: String?) {
        this.firstName = value ?: ""
        viewState.showFirstNameError(false)
    }

    override fun performChangeLastName(value: String?) {
        this.lastName = value ?: ""
        viewState.showLastNameError(false)
    }

    override fun performChangeMiddleName(value: String?) {
        this.middleName = value ?: ""
        viewState.showMiddleNameError(false)
    }

    override fun performSetNoMiddleName(isHas: Boolean) {
        this.isMiddleNameAbsent = isHas
        viewState.enableMiddleNameInput(isHas)
        viewState.showMiddleNameError(false)
    }

    private fun performDataChange() {
        viewState.enableBtnSave(isDataValid())
    }

    private fun isDataValid(): Boolean {
        val firstNameValid = !firstName.isNullOrBlank()
        val lastNameValid = !lastName.isNullOrBlank()
        val middleNameValid = if (isMiddleNameAbsent) true else !middleName.isNullOrBlank()

        return firstNameValid && lastNameValid && middleNameValid
    }

    private fun showErrors() {
        viewState.apply {
            showFirstNameError(firstName.isNullOrBlank())
            showLastNameError(lastName.isNullOrBlank())
            if (!isMiddleNameAbsent) showMiddleNameError(middleName.isNullOrBlank())
        }
    }

}