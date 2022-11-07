package com.example.ui.state.max.education

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.AcademicDegreeModel
import com.example.data.models.EducationModel
import com.example.data.models.FieldDetails
import com.example.data.models.ToggleIntModel
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.ui.state.max.MaxStateMainInfoContract
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCustomProgressBarLoadingDialog
import javax.inject.Inject
import kotlin.math.abs

@InjectViewState
class MaxStateEducationPresenter
@Inject constructor(
    private val appData: AppData,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : BasePresenter<MaxStateEducationContract.View>(appData), MaxStateEducationContract.Presenter {

    var screen: Int = 1
    private var mDy = 0f

    override fun attachView(view: MaxStateEducationContract.View?) {
        super.attachView(view)
        viewState.setAppBarElevation(mDy)
    }

    override fun changeAppBarElevation(value: Int) {
        mDy = abs(value / 10f)
        viewState.setAppBarElevation(mDy)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setAppBarElevation(mDy)
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
                //viewState.goToNext()
            }, {
                it.printStackTrace()
                viewState.showUpdateError(it.message)
            })
    }

    fun getEmail() = appData.getUserNew().email

    override fun sendEmail(email: String) {
        compositeDisposable += authRepository.registerEmailResend(email)
            .performOnBackgroundOutOnMain()
            .subscribe({
                appData.updateUserNew {
                    this.email = FieldDetails(email, null, true, false, false, null)
                }
                viewState.showChangeEmailComplete(email)
            }, {
                it.printStackTrace()
            })
    }
}