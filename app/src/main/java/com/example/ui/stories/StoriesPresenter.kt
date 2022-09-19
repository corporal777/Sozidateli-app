package com.example.ui.stories

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.data.AppData
import com.example.ui.base.BasePresenter
import com.example.ui.main.MainContract
import javax.inject.Inject

@InjectViewState
class StoriesPresenter
@Inject constructor(
    private val appData: AppData
) : BasePresenter<StoriesContract.View>(appData), StoriesContract.Presenter  {


    override fun onStoriesComplete() {
        appData.isStoriesShown = true
        viewState.showAuthorization()
    }
}
