package com.example.ui.state.max.education

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.AcademicDegreeModel
import com.example.data.models.EducationModel
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class MaxStateEducationPresenter
@Inject constructor(
        private val appData: AppData,
        private val userRepository: UserRepository
): BasePresenter<MaxStateEducationContract.View>(), MaxStateEducationContract.Presenter {

    var screen: Int = 1

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
        viewState.navigateUp()
    }

    override fun onSaveEducationClick(educationLevel: Int?, educationsList: List<EducationModel>?, degree: List<AcademicDegreeModel>?) {
        compositeDisposable += userRepository.updateUserEducationScreen(educationLevel, educationsList, degree)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    compositeDisposable += userRepository.checkUserProfileSingle()
                            .performOnBackgroundOutOnMain()
                            .subscribe({
                                viewState.goToNext()
                            },{
                                viewState.goToNext()
                            })
                    //viewState.goToNext()
                }, {
                    it.printStackTrace()
                    viewState.showUpdateError(it.message)
                })
    }
}