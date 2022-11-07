package com.example.ui.state.max.work

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.WorkExperienceServerModel
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.ui.state.max.MaxStateMainInfoContract
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCustomProgressBarLoadingDialog
import javax.inject.Inject
import kotlin.math.abs

@InjectViewState
class MaxStateWorkPresenter
@Inject constructor(
    private val appData: AppData,
    private val userRepository: UserRepository
) : BasePresenter<MaxStateWorkContract.View>(appData), MaxStateWorkContract.Presenter {

    var screen: Int = 1
    private var mDy = 0f

    override fun attachView(view: MaxStateWorkContract.View?) {
        super.attachView(view)
        viewState.setAppBarElevation(mDy)
    }

    override fun changeAppBarElevation(value: Int) {
        mDy = abs(value / 10f)
        viewState.setAppBarElevation(mDy)
    }

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

    override fun onClickClose() {
        viewState.setClickClose(screen)
    }

    override fun onSaveWorkClick(data: WorkExperienceServerModel) {
        compositeDisposable += userRepository.updateWorkExperience(data)
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
}