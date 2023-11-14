package com.example.ui.image

import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import com.example.ui.base.BaseContract
import com.example.util.AddToEndSingleByTagStateStrategy
import moxy.MvpView
import moxy.viewstate.strategy.StateStrategyType
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.SingleState

interface ImageViewContract {
    interface View : MvpView {
        @SingleState
        fun setImage(bitmap: Bitmap)

        @SingleState
        fun showError()

        @AddToEndSingle
        fun findImageBitmap(url: String)

        @AddToEndSingle
        fun findImageBitmap(@DrawableRes resource: Int)

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "transition_name")
        fun setCustomTransitionName(transitionName: String)

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "transition_name")
        fun setDefaultTransitionName()
    }

    interface Presenter : BaseContract.Presenter {
        fun onBitmapFound(bitmap: Bitmap)
        fun onBitmapFoundFailed(t: Exception)
    }
}
