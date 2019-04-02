package com.example.ui.mapTabs.buildingScheme

import com.arellomobile.mvp.InjectViewState
import com.example.data.UserEventData
import com.example.ui.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class BuildingSchemePresenter
@Inject constructor(
        userEventData: UserEventData
) : BasePresenter<BuildingSchemeContract.View>(), BuildingSchemeContract.Presenter {

    private val mapInfo = userEventData.mapInfo
    private var scroll = 0

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        mapInfo?.let {
            viewState.setSchemeData(it.int_scheme,it.int_scheme_descriptions)
        }
    }

    override fun attachView(view: BuildingSchemeContract.View?) {
        super.attachView(view)
        viewState.changeScrollY(scroll)
    }

    override fun onImageClick(){
        mapInfo?.int_scheme?.let {
            viewState.showImage(it)
        }
    }

    override fun onScrollPositionChange(scroll: Int) {
        this.scroll = scroll
    }
}
