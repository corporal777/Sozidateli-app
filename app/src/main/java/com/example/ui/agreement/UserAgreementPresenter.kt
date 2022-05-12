package com.example.ui.agreement

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.repository.CommonRepository
import com.example.ui.base.BasePresenter
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class UserAgreementPresenter
@Inject constructor(
        private val commonRepository: CommonRepository,
        appData: AppData
) : BasePresenter<UserAgreementContract.View>(appData), UserAgreementContract.Presenter {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        /*compositeDisposable += commonRepository.getAgreement()
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple {
                    viewState.setTitle(it.title)
                    viewState.setContent(it.text)
                }*/
    }
}
