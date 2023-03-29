package com.example.ui.state.maxNew.mainInfo

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
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
) : BaseMaxStatePresenter<MaxStatusContactsContract.View>(appData, userRepository),
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
            compositeDisposable += userRepository.updateProfile(appData.getId(), data)
                .doOnSuccess {
                    appData.updateUserNew {
                        name = it.name
                        middleName = it.middleName
                        lastName = it.lastName
                        birthday = it.birthday
                        gender = it.gender
                        notes = it.notes
                        address = it.address
                        contactInformation.site = it.contactInformation.site
                        contactInformation.socialLinks = it.contactInformation.socialLinks
                        phone = it.phone
                    }
                }
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
