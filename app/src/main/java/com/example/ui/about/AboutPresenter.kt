package com.example.ui.about

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.ui.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class AboutPresenter
@Inject constructor( appData: AppData
) : BasePresenter<AboutContract.View>(appData), AboutContract.Presenter {

    override fun attachView(view: AboutContract.View?) {
        super.attachView(view)
    }
}
