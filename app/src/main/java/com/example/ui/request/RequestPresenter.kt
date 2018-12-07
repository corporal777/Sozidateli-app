package com.example.ui.request

import com.arellomobile.mvp.InjectViewState
import com.example.ui.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class RequestPresenter
@Inject constructor(
) : BasePresenter<RequestContract.View>(), RequestContract.Presenter {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.enableActionButton(true)
    }

    override fun onCloseClick() = viewState.navigateUp()
}
