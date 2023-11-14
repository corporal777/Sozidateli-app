package com.example.ui.state.maxNew.education

import com.example.data.AppData
import com.example.data.models.AcademicDegreeModel
import com.example.data.models.EducationModel
import com.example.data.models.ToggleIntModel
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
class MaxStatusEducationPresenter
@Inject constructor(
    val appData: AppData,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : BaseMaxStatePresenter<MaxStatusEducationContract.View>(appData, userRepository, authRepository),
    MaxStatusEducationContract.Presenter {

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
            .subscribe({
                val user = it.value
                if (user == null) viewState.navigateUp()
                else if (isUpdating) isUpdating = false
                else viewState.setEducationData(user)
            }, {
                it.printStackTrace()
                viewState.navigateUp()
            })
    }

    override fun onSaveEducationClick(
        educationLevel: ToggleIntModel?,
        educationsList: List<EducationModel>?,
        degree: List<AcademicDegreeModel>?
    ) {
        isUpdating = true
        compositeDisposable += userRepository.updateUserEducation(
            educationLevel,
            educationsList,
            degree
        )
            .performOnBackgroundOutOnMain()
            .withInfinityCustomLoading(viewState)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = { checkNextScreen() }
            )
    }

}