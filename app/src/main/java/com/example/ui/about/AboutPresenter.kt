package com.example.ui.about

import com.arellomobile.mvp.InjectViewState
import com.example.ui.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class AboutPresenter
@Inject constructor(
) : BasePresenter<AboutContract.View>(), AboutContract.Presenter {

    override fun attachView(view: AboutContract.View?) {
        super.attachView(view)
    }
}
