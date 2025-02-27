package com.example.ui.about

import com.example.data.AppData
import com.example.ui.base.BasePresenter
import io.reactivex.Completable
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import withDelay
import withProgressBarDialogLoading
import withProgressBarLoading
import javax.inject.Inject

@InjectViewState
class AboutPresenter
@Inject constructor(
    appData: AppData
) : BasePresenter<AboutContract.View>(appData), AboutContract.Presenter {


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
    }
}
