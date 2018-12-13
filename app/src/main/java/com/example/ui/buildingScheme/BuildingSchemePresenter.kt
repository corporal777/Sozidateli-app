package com.example.ui.buildingScheme

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.ui.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class BuildingSchemePresenter
@Inject constructor(
        appData: AppData
) : BasePresenter<BuildingSchemeContract.View>(), BuildingSchemeContract.Presenter {

    private val place = appData.event!!.place
    private var scroll = 0

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setPlaceData(place)
    }

    override fun attachView(view: BuildingSchemeContract.View?) {
        super.attachView(view)
        viewState.changeScrollY(scroll)
    }

    override fun onImageClick() = viewState.showImage(place.schemeImage)

    override fun onScrollPositionChange(scroll: Int) {
        this.scroll = scroll
    }
}
