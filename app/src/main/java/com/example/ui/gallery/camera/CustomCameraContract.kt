package com.example.ui.gallery.camera

import android.net.Uri
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.SingleStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.base.BaseContract
import com.example.util.AddToEndSingleByTagStateStrategy

interface CustomCameraContract {

    interface View : MvpView {

        @StateStrategyType(SingleStateStrategy::class)
        fun setImage(uri: Uri?)

        @StateStrategyType(SkipStrategy::class)
        fun startCameraPreview()

        @StateStrategyType(SkipStrategy::class)
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