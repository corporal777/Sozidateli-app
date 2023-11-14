package com.example.ui.favoritesTab

import com.example.data.AppData
import com.example.ui.base.BasePresenter
import moxy.InjectViewState
import javax.inject.Inject

@InjectViewState
class FavoriteTabsPresenter
@Inject constructor(appData: AppData) :
    BasePresenter<FavoriteTabsContract.View>(appData), FavoriteTabsContract.Presenter {

    override fun attachView(view: FavoriteTabsContract.View?) {
        super.attachView(view)
    }
}
