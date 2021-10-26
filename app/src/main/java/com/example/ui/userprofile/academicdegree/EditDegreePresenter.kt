package com.example.ui.userprofile.academicdegree

import com.arellomobile.mvp.InjectViewState
import com.example.BuildConfig
import com.example.data.AppData
import com.example.data.models.UserEditDataType
import com.example.ui.base.BasePresenter
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import javax.inject.Inject

@InjectViewState
class EditDegreePresenter
@Inject constructor(
        private val appData: AppData
) : BasePresenter<EditDegreeContract.View>(appData), EditDegreeContract.Presenter {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setTitle()
        compositeDisposable += appData.userChangeSubject
                .performOnBackgroundOutOnMain()
                .subscribe({
                    val user = it.value ?: throw RuntimeException("Edit null user")
                    viewState.setUserData(user)
                }, {
                    it.printStackTrace()
                    viewState.navigateUp()
                })
    }
}