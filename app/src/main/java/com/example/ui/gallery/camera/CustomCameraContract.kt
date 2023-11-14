package com.example.ui.gallery.camera

import android.net.Uri
import com.example.ui.base.BaseContract
import com.example.util.AddToEndSingleByTagStateStrategy
import moxy.MvpView
import moxy.viewstate.strategy.SingleStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import moxy.viewstate.strategy.alias.SingleState
import moxy.viewstate.strategy.alias.Skip

interface CustomCameraContract {

    interface View : MvpView {

        @SingleState
        fun setImage(uri: Uri?)

        @Skip
        fun startCameraPreview()

        @Skip
        fun stopCameraPreview()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "transition_name")
        fun setCustomTransitionName(transitionName: String)

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "transition_name")
        fun setDefaultTransitionName()

        @StateStrategyType(SingleStateStrategy::class)
        fun setCapturedImage(uri: Uri)
    }

    interface Presenter : BaseContract.Presenter {
        fun onStartPreview()
    }
}