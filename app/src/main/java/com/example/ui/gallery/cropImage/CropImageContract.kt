package com.example.ui.gallery.cropImage

import android.graphics.Bitmap
import android.net.Uri
import com.example.ui.base.BaseContract
import com.example.util.AddToEndSingleByTagStateStrategy
import io.reactivex.Maybe
import moxy.MvpView
import moxy.viewstate.strategy.StateStrategyType
import moxy.viewstate.strategy.alias.SingleState
import moxy.viewstate.strategy.alias.Skip

interface CropImageContract {
    interface View : MvpView {
        @SingleState
        fun setImage(uri: Uri?)

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "transition_name")
        fun setCustomTransitionName(transitionName: String)

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "transition_name")
        fun setDefaultTransitionName()

        @Skip
        fun showImageCrop(uri: Uri?, bitmap: Bitmap?)

        @Skip
        fun closeCropActivity()

        @Skip
        fun showProgressDialog()

        @Skip
        fun hideProgressDialog()
    }

    interface Presenter : BaseContract.Presenter {
        fun onShowImageCrop(uri: Uri?, width : Int, height : Int)
        fun saveCroppedImage(request : Maybe<Bitmap>)
    }
}