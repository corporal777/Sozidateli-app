package com.example.ui.splash

import com.arellomobile.mvp.InjectViewState
import com.example.ui.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class SplashPresenter
@Inject constructor() : BasePresenter<SplashContract.View>(), SplashContract.Presenter
