package com.example.ui.state.maxNew.mainInfo

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.state.maxNew.base.BaseMaxStatePresenter
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCustomProgressBarLoadingDialog
import withProgressBarLoadingDialog
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
        compositeDisposable += appData.userNewChangeSubject
            .performOnBackgroundOutOnMain()
            .let {
                if (isFirstLaunch) {
                    isFirstLaunch = false
                    it.withProgressBarLoadingDialog(viewState)
                } else it
            }
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                    viewState.navigateUp()
                },
                onNext = {
                    val user = it.value ?: throw RuntimeException("Edit null user")
                    viewState.setPersonalData(user)
                })
    }

    override fun updateFiles(data: MutableMap<String, Any?>) {
        if (data.isEmpty()) {
            viewState.navigateUp()
            return
        } else {
            compositeDisposable += userRepository.updateUserProfile(appData.getId(), data)
                .performOnBackgroundOutOnMain()
                .withCustomProgressBarLoadingDialog(viewState)
                .subscribe({
                    checkNextScreen()
                }, {
                    it.printStackTrace()
                    viewState.showUpdateError(it.message)
                })
        }
    }

}
