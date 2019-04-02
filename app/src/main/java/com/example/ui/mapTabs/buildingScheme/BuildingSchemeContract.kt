package com.example.ui.mapTabs.buildingScheme

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Place
import com.example.ui.base.BaseContract

interface BuildingSchemeContract {
    interface View : BaseContract.View {

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setSchemeData(image:String?,description:String?)

        @StateStrategyType(SkipStrategy::class)
        fun showImage(url: String)

        @StateStrategyType(SkipStrategy::class)
        fun changeScrollY(scroll: Int)
    }

    interface Presenter : BaseContract.Presenter {
        fun onImageClick()
        fun onScrollPositionChange(scroll: Int)
    }
}
