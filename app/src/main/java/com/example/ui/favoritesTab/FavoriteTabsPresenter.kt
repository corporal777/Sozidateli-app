package com.example.ui.favoritesTab

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.ui.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class FavoriteTabsPresenter
@Inject constructor(appData: AppData) :
    BasePresenter<FavoriteTabsContract.View>(appData), FavoriteTabsContract.Presenter {

    var currentPosition = 0
    private var isFirstLaunch = true


    override fun attachView(view: FavoriteTabsContract.View?) {
        super.attachView(view)
        if (isFirstLaunch) isFirstLaunch = false
        else viewState.setCurrentFragment(currentPosition)
    }
}
