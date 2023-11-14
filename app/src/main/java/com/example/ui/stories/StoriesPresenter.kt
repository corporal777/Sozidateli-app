package com.example.ui.stories

import com.example.data.AppData
import com.example.ui.base.BasePresenter
import moxy.InjectViewState
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
