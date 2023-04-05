package com.example.ui.state.maxNew.work

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.FieldDetails
import com.example.data.models.UserDetail
import com.example.data.models.WorkExperienceServerModel
import com.example.repository.AuthRepository
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.state.maxNew.base.BaseMaxStatePresenter
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCustomProgressBarLoadingDialog
import withProgressBarLoadingDialog
import javax.inject.Inject

@InjectViewState
class MaxStatusWorkPresenter
@Inject constructor(
    val appData: AppData,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : BaseMaxStatePresenter<MaxStatusWorkContract.View>(appData, userRepository, authRepository),
    MaxStatusWorkContract.Presenter {

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
                    viewState.setWorkData(user)
                })
    }

    override fun onSaveWorkClick(data: WorkExperienceServerModel) {
        compositeDisposable += userRepository.updateWorkExperience(data)
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