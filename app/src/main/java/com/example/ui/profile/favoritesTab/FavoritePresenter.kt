package com.example.ui.profile.favoritesTab

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.ui.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class FavoritePresenter @Inject constructor(appData: AppData) : BasePresenter<FavoriteContract.View>(appData), FavoriteContract.Presenter
