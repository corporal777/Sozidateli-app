package com.example.ui.editeducation

import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.AcademicDegreeModel
import com.example.data.models.EducationModel
import com.example.data.models.ToggleIntModel
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withInfinityCustomLoading
import javax.inject.Inject

@InjectViewState
class EditEducationPresenter
@Inject constructor(
    private val appData: AppData,
    private val userRepository: UserRepository
) : BasePresenter<EditEducationContract.View>(appData), EditEducationContract.Presenter {


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += appData.userChangeSubject
            .performOnBackgroundOutOnMain()
            .subscribe({
                val user = it.value
                if (user != null) viewState.setEducationData(user)
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
        userRepository.updateUserEducation(educationLevel, educationsList, degree)
            .map { userRepository.checkUserProfileSingle() }
            .performOnBackgroundOutOnMain()
            .withInfinityCustomLoading(viewState)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = { viewState.navigateUp() }
            ).call(compositeDisposable)
    }

    fun getBaseUserState() = appData.hasBaseState
    fun getMaxUserState() = appData.hasMaxState
}