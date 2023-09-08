package com.example.ui.views.galleryView.cropImage

import android.graphics.Bitmap
import android.net.Uri
import androidx.annotation.DrawableRes
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SingleStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.base.BaseContract
import com.example.util.AddToEndSingleByTagStateStrategy
import com.isseiaoki.simplecropview.CropImageView

interface CropImageContract {
    interface View : MvpView {
        @StateStrategyType(SingleStateStrategy::class)
        fun setImage(uri: Uri?)

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "transition_name")
        fun setCustomTransitionName(transitionName: String)

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "transition_name")
        fun setDefaultTransitionName()

        @StateStrategyType(SkipStrategy::class)
        fun showImageCrop(uri: Uri?)
    }

    interface Presenter : BaseContract.Presenter {
        fun onShowImageCrop(uri: Uri?)
        fun saveCroppedImage(cropView : CropImageView)
    }
}