package com.example.ui.gallery.cropImage

import android.graphics.Bitmap
import android.net.Uri
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.SingleStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.base.BaseContract
import com.example.ui.gallery.cropImage.cropHelper.CropImageView
import com.example.util.AddToEndSingleByTagStateStrategy
import io.reactivex.Maybe

interface CropImageContract {
    interface View : MvpView {
        @StateStrategyType(SingleStateStrategy::class)
        fun setImage(uri: Uri?)

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "transition_name")
        fun setCustomTransitionName(transitionName: String)

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "transition_name")
        fun setDefaultTransitionName()

        @StateStrategyType(SkipStrategy::class)
        fun showImageCrop(uri: Uri?, bitmap: Bitmap?)

        @StateStrategyType(SkipStrategy::class)
        fun closeCropActivity()

        @StateStrategyType(SkipStrategy::class)
        fun showProgressDialog()

        @StateStrategyType(SkipStrategy::class)
        fun hideProgressDialog()
    }

    interface Presenter : BaseContract.Presenter {
        fun onShowImageCrop(uri: Uri?, width : Int, height : Int)
        fun saveCroppedImage(request : Maybe<Bitmap>)
    }
}