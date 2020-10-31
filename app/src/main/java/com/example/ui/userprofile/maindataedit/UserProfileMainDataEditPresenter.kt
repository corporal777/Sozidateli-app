package com.example.ui.userprofile.maindataedit

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.user.User
import com.example.repository.UserRepository
import com.example.ui.userprofile.base.BaseUserProfilePresenter
import com.example.util.USER_MIDDLE_NAME_EMPTY
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class UserProfileMainDataEditPresenter @Inject constructor(
        appData: AppData,
        private val userRepository: UserRepository
) : BaseUserProfilePresenter<UserProfileMainDataEditContract.View>(appData), UserProfileMainDataEditContract.Presenter {

    override fun onSaveClick(lastName: String?, name: String?, middleName: String?, noMiddleName: Boolean) {
        var hasError = isInputEmpty(lastName, viewState::showEmptyLastNameError)
        hasError = isInputEmpty(name, viewState::showEmptyNameError) || hasError

        val middleNameInternal = if (noMiddleName) USER_MIDDLE_NAME_EMPTY else middleName
        hasError = isInputEmpty(middleNameInternal, viewState::showEmptyMiddleNameError) || hasError

        if (!hasError) {
            val updateMap = mutableMapOf<String, Any?>()
            if (lastName != user.user_last_name) updateMap[User.FIELD_USER_LAST_NAME] = lastName
            if (name != user.user_name) updateMap[User.FIELD_USER_NAME] = name
            if (middleNameInternal != user.user_middle_name) updateMap[User.FIELD_USER_NAME] = middleNameInternal

            if (updateMap.isEmpty()) {
                viewState.navigateUp()
            } else {
                updateUser(updateMap)
            }
        }
    }

    private fun updateUser(data: Map<String, Any?>) {
        compositeDisposable += userRepository.updateUser(data)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple(
                        onSuccess = {
                            compositeDisposable.dispose()
                            updateUserInternal {
                                user_name = it.user_name
                                user_last_name = it.user_last_name
                                user_middle_name = it.user_middle_name
                            }
                            viewState.navigateUp()
                        }
                )
    }

    private fun isInputEmpty(text: String?, onInvalidAction: () -> Unit): Boolean {
        return if (text.isNullOrBlank()) {
            onInvalidAction()
            true
        } else {
            false
        }
    }
}
