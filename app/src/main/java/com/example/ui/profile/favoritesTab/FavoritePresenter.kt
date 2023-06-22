package com.example.ui.profile.favoritesTab

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.ui.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class FavoritePresenter
@Inject constructor(appData: AppData) :
    BasePresenter<FavoriteContract.View>(appData), FavoriteContract.Presenter {

    var currentPosition = 0
    private var isFirstLaunch = true


    override fun attachView(view: FavoriteContract.View?) {
        super.attachView(view)
        if (isFirstLaunch) isFirstLaunch = false
        else viewState.setCurrentFragment(currentPosition)
    }
}
