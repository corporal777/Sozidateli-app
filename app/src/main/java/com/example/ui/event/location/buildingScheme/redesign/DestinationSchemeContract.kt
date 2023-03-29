package com.example.ui.event.location.buildingScheme.redesign

import android.graphics.Bitmap
import android.util.SparseIntArray
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Place
import com.example.ui.base.BaseContract

interface DestinationSchemeContract {

    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setScheme(places: List<Place>, page: Int)

        @StateStrategyType(SkipStrategy::class)
        fun showImage(url: String?)

        @StateStrategyType(SkipStrategy::class)
        fun setAppBarShadow(value: Float)
    }

    interface Presenter : BaseContract.Presenter {
        fun onImageClick(url: String?)
        fun onScrollChangeOffset(scroll: Int)
        fun onPageChange(position: Int)
    }

}