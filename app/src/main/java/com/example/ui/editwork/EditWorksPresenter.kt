package com.example.ui.editwork

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.WorkExperienceServerModel
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withInfinityCustomLoading
import withProgressBarDialogLoading
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
                val user = it.value
                if (user != null) viewState.setWorkData(user)
            }, {
                it.printStackTrace()
                viewState.navigateUp()
            })
    }

    override fun onSaveWorkClick(data: WorkExperienceServerModel) {
        compositeDisposable += userRepository.updateWorkExperience(data)
            .map { userRepository.checkUserProfileSingle() }
            .performOnBackgroundOutOnMain()
            .withInfinityCustomLoading(viewState)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = { viewState.navigateUp() }
            )
    }

    fun getBaseUserState() = appData.hasBaseState
    fun getMaxUserState() = appData.hasMaxState
}