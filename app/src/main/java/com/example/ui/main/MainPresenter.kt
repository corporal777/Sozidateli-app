package com.example.ui.main

import com.arellomobile.mvp.InjectViewState
import com.example.ui.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class MainPresenter
@Inject constructor(
) : BasePresenter<MainContract.View>(), MainContract.Presenter
