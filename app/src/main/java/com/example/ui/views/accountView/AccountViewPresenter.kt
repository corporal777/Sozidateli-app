package com.example.ui.views.accountView

import com.arellomobile.mvp.InjectViewState
import com.example.repository.DummyRepository
import com.example.ui.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class AccountViewPresenter @Inject constructor(
        private val dummyRepository: DummyRepository
) : BasePresenter<AccountViewContract.View>(), AccountViewContract.Presenter {


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setChatCount(3)
    }
}
