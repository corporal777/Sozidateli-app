package com.example.ui.map

import com.arellomobile.mvp.InjectViewState
import com.example.ui.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class MapPresenter
@Inject constructor(
) : BasePresenter<MapContract.View>(), MapContract.Presenter {

}
