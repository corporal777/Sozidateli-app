package com.example.ui.state.maxNew.contacts

import com.example.data.AppData
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.state.maxNew.base.BaseMaxStatePresenter
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import withInfinityCustomLoading
import withProgressBarLoading
import javax.inject.Inject

@InjectViewState
class MaxStatusContactsPresenter
@Inject constructor(
    val appData: AppData,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : BaseMaxStatePresenter<MaxStatusContactsContract.View>(appData, userRepository, authRepository),
    MaxStatusContactsContract.Presenter {

    private var isFirstLaunch = true

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += appData.userChangeSubject
            .performOnBackgroundOutOnMain()
            .let {
                if (isFirstLaunch) {
                    isFirstLaunch = false
                    it.withProgressBarLoading(viewState)
                } else it
            }
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                    viewState.navigateUp()
                },
                onNext = {
                    val user = it.value
                    if (user == null) viewState.navigateUp()
                    else if (isUpdating) isUpdating = false
                    else viewState.setPersonalData(user)
                })
    }

    override fun saveContactsClick(data: MutableMap<String, Any?>) {
        isUpdating = true
        if (data.isEmpty()) {
            viewState.navigateUp()
            return
        } else {
            compositeDisposable += userRepository.updateUserProfile(appData.getId(), data)
                .performOnBackgroundOutOnMain()
                .withInfinityCustomLoading(viewState)
                .subscribeSimple(
                    onError = { onReceiveError(it) },
                    onSuccess = { checkNextScreen() }
                )
        }
    }

}
