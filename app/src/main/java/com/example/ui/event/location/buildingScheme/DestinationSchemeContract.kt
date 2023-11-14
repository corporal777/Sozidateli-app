package com.example.ui.event.location.buildingScheme

import com.example.data.models.Place
import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface DestinationSchemeContract {

    interface View : BaseContract.View {
        @OneExecution
        fun setScheme(places: List<Place>, page: Int)

        @Skip
        fun showImage(url: String?)

        @Skip
        fun setAppBarShadow(value: Float)
    }

    interface Presenter : BaseContract.Presenter {
        fun onImageClick(url: String?)
        fun onScrollChangeOffset(scroll: Int)
        fun onPageChange(position: Int)
    }

}