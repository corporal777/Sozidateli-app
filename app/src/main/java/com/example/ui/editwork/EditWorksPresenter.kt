package com.example.ui.editwork

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.WorkExperienceServerModel
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCustomProgressBarLoadingDialog
import javax.inject.Inject

@InjectViewState
class EditWorksPresenter
@Inject constructor(
    private val appData: AppData,
    private val userRepository: UserRepository
) : BasePresenter<EditWorksContract.View>(appData), EditWorksContract.Presenter {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += appData.userNewChangeSubject
            .performOnBackgroundOutOnMain()
            .subscribe({
                val user = it.value ?: throw RuntimeException("Edit null user")
                viewState.apply {
                    setWorkData(user)
                }
            }, {
                it.printStackTrace()
                viewState.navigateUp()
            })
    }

    override fun attachView(view: EditWorksContract.View?) {
        super.attachView(view)
    }

    override fun onSaveWorkClick(data: WorkExperienceServerModel) {
        compositeDisposable += userRepository.updateWorkExperience(data)
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribe({
                viewState.navigateUp()
            }, {
                onReceiveError(it)
            })
    }

    fun getBaseUserState() = appData.hasBaseState
    fun getMaxUserState() = appData.hasMaxState
}