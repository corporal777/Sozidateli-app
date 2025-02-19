package com.example.ui.userprofile.common.name

import com.example.data.AppData
import com.example.data.models.FieldDetails
import com.example.data.models.UserDetail
import com.example.data.models.UserDetail.Companion.USER_LAST_NAME
import com.example.data.models.UserDetail.Companion.USER_NAME
import com.example.extensions.removeAllDoubleSpaces
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.base.bottomSheet.BaseBSPresenter
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import withCustomLoading
import javax.inject.Inject

@InjectViewState
class ChangeNamePresenter
@Inject constructor(
    private val userRepository: UserRepository,
    private val appData: AppData,
) : BaseBSPresenter<ChangeNameContract.View>(appData), ChangeNameContract.Presenter {

    var userDetail: UserDetail? = null
    private var firstName = ""
    private var lastName = ""
    private var middleName = ""
    private var isMiddleNameAbsent = false

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        firstName = userDetail?.name ?: ""
        lastName = userDetail?.lastName ?: ""
        middleName = userDetail?.middleName?.value ?: ""
        isMiddleNameAbsent = userDetail?.middleName?.absent ?: middleName.isNullOrBlank() || middleName == "-"

        viewState.setUserName(firstName, lastName, middleName, isMiddleNameAbsent)
    }

    override fun onSaveNameClick() {
        if (isDataValid()) {
            compositeDisposable += userRepository.updateUserProfile(appData.getId(),
                mutableMapOf<String, Any?>().apply {
                    if (firstName != userDetail?.name) put(USER_NAME, firstName)
                    if (lastName != userDetail?.lastName) put(USER_LAST_NAME, lastName)

                    val midName = if (middleName.isNullOrBlank()) FieldDetails(value = null, absent = true)
                    else FieldDetails(value = middleName.removeAllDoubleSpaces())
                    put(UserDetail.USER_MIDDLE_NAME, midName)
                }
            )
                .flatMap { userRepository.checkUserProfileSingle() }
                .performOnBackgroundOutOnMain()
                .withCustomLoading(viewState)
                .subscribeBy(
                    onError = { onReceiveError(it) },
                    onSuccess = { viewState.hideBottomSheetFragment() }
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