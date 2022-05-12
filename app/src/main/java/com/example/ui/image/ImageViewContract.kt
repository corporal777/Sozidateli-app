package com.example.ui.image

import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SingleStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.base.BaseContract
import com.example.util.AddToEndSingleByTagStateStrategy

interface ImageViewContract {
    interface View : MvpView {
        @StateStrategyType(SingleStateStrategy::class)
        fun setImage(bitmap: Bitmap)

        @StateStrategyType(SingleStateStrategy::class)
        fun showError()

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun findImageBitmap(url: String)

        @StateStrategyType(AddToEndSingleStrategy::class)
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
