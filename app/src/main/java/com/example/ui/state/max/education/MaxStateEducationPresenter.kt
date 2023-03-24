package com.example.ui.state.max.education

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.*
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withCustomProgressBarLoadingDialog
import javax.inject.Inject

@InjectViewState
class MaxStateEducationPresenter
@Inject constructor(
    private val appData: AppData,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : BasePresenter<MaxStateEducationContract.View>(appData), MaxStateEducationContract.Presenter {

    var screen: Int = 1

    override fun attachView(view: MaxStateEducationContract.View?) {
        super.attachView(view)

    }


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += appData.userNewChangeSubject
            .performOnBackgroundOutOnMain()
            .subscribe({
                val user = it.value ?: throw RuntimeException("Edit null user")
                viewState.apply {
                    setEducationData(user)
                }
            }, {
                it.printStackTrace()
                viewState.navigateUp()
            })
    }

    override fun onClickClose() {
        viewState.setClickClose(screen)
    }

    override fun onSaveEducationClick(
        educationLevel: ToggleIntModel?,
        educationsList: List<EducationModel>?,
        degree: List<AcademicDegreeModel>?
    ) {
        compositeDisposable += userRepository.updateUserEducationScreen(
            educationLevel,
            educationsList,
            degree
        )
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribe({
                compositeDisposable += userRepository.checkUserProfileSingle()
                    .performOnBackgroundOutOnMain()
                    .subscribe({
                        viewState.goToNext()
                    }, {
                        viewState.goToNext()
                    })
            }, {
                it.printStackTrace()
                viewState.showUpdateError(it.message)
            })
    }

    fun getEmail() = appData.getUserNew().email


    override fun checkEmailIsUnique(email: String) {
        compositeDisposable += userRepository.checkEmailPhone(email, null)
            .withCheckInternetConnectivity()
            .withCustomProgressBarLoadingDialog(viewState)
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    viewState.showEmailIsNotUnique(email)
                },
                onComplete = {
                    onShowEmailConfirm(email)
                })
    }


    override fun onShowEmailConfirm(email: String) {
        compositeDisposable += userRepository.updateUserProfile(
            appData.getId(),
            mapOf(UserDetail.USER_EMAIL to FieldDetails(value = email))
        ).ignoreElement()
            .andThen(authRepository.registerEmailResend(email))
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple {
                appData.updateUserNew {
                    this.email = FieldDetails(value = email)
                }
                viewState.showEmailConfirmation(email)
            }
    }

}