package com.example.ui.stories

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import javax.inject.Inject

@InjectViewState
class StoriesPresenter
@Inject constructor(
) : MvpPresenter<StoriesContract.View>(), StoriesContract.Presenter {
    override fun onStoriesComplete() {
    }
}
