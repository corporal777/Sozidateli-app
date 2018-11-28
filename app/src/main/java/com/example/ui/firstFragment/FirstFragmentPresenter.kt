package com.example.ui.firstFragment

import com.arellomobile.mvp.InjectViewState
import com.example.ui.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class FirstFragmentPresenter
@Inject constructor(
) : BasePresenter<FirstFragmentContract.View>(), FirstFragmentContract.Presenter
