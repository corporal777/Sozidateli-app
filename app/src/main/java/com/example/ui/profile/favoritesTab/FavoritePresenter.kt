package com.example.ui.profile.favoritesTab

import com.arellomobile.mvp.InjectViewState
import com.example.ui.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class FavoritePresenter @Inject constructor() : BasePresenter<FavoriteContract.View>(), FavoriteContract.Presenter
