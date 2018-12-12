package com.example.ui.mapTabs

import com.arellomobile.mvp.InjectViewState
import com.example.ui.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class MapTabsPresenter
@Inject constructor(
) : BasePresenter<MapTabsContract.View>(), MapTabsContract.Presenter {

}
