package com.example.ui.event.location.buildingScheme

import com.arellomobile.mvp.InjectViewState
import com.example.data.UserEventData
import com.example.ui.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class BuildingSchemePresenter
@Inject constructor(
        userEventData: UserEventData
) : BasePresenter<BuildingSchemeContract.View>(), BuildingSchemeContract.Presenter {

    private var scroll = 0

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
    }

    override fun attachView(view: BuildingSchemeContract.View?) {
        super.attachView(view)
        viewState.changeScrollY(scroll)
    }

    override fun onImageClick() {
    }

    override fun onScrollPositionChange(scroll: Int) {
    }
}
