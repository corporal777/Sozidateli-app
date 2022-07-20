package com.example.ui.editeducation

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.AcademicDegreeModel
import com.example.data.models.EducationModel
import com.example.data.models.ToggleIntModel
import com.example.data.models.WorkExperienceServerModel
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class EditEducationPresenter
@Inject constructor(
        private val appData: AppData,
        private val userRepository: UserRepository
) : BasePresenter<EditEducationContract.View>(appData), EditEducationContract.Presenter {

    private var mDy = 0

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setAppBarElevation(0f)
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

    override fun attachView(view: EditEducationContract.View?) {
        super.attachView(view)
        viewState.setAppBarElevation(Math.abs(mDy / 10f))
    }

    override fun changeAppBarElevation(value: Int) {
        mDy += value
        viewState.setAppBarElevation(Math.abs(mDy / 10f))
    }

    override fun onSaveEducationClick(educationLevel: ToggleIntModel?, educationsList: List<EducationModel>?, degree: List<AcademicDegreeModel>?) {
        compositeDisposable += userRepository.updateUserEducationScreen(educationLevel, educationsList, degree)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    viewState.navigateUp()
                }, {
                    it.printStackTrace()
                    viewState.showUpdateError(it.message)
                })
    }

    fun getBaseUserState() = appData.hasBaseState
    fun getMaxUserState() = appData.hasMaxState
}