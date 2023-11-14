package com.example.ui.splash

import com.example.data.AppData
import com.example.ui.base.BasePresenter
import moxy.InjectViewState
import javax.inject.Inject

@InjectViewState
class SplashPresenter
@Inject constructor(appData: AppData) : BasePresenter<SplashContract.View>(appData), SplashContract.Presenter
