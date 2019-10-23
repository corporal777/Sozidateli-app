package com.example.ui.event.location.buildingScheme

import android.util.SparseIntArray
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Place
import com.example.ui.base.BaseContract

interface BuildingSchemeContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setPlaces(places: List<Place>, scrollPositions: SparseIntArray)

        @StateStrategyType(SkipStrategy::class)
        fun showImage(url: String)
    }

    interface Presenter : BaseContract.Presenter {
        fun onImageClick(place: Place, position: Int)
        fun onScrollPositionChange(scroll: Int, position: Int)
    }
}
