package com.example.ui.stories

import com.example.data.AppData
import com.example.ui.base.BasePresenter
import moxy.InjectViewState
import moxy.MvpPresenter
import javax.inject.Inject

@InjectViewState
class StoriesPresenter
@Inject constructor(
    private val appData: AppData
) : MvpPresenter<StoriesContract.View>(), StoriesContract.Presenter  {


    override fun onStoriesComplete() {
        appData.isStoriesShown = true
        viewState.showAuthorization()
    }
}
