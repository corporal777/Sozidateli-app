package com.example.ui.profile.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.UserDetail
import com.example.ui.base.bottomSheet.BaseBottomSheetContract

interface ProfileDataContract {

    interface View : BaseBottomSheetContract.View{

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setImage(image : Bitmap)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setName(userName : String, userLink : String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showShareImage(uri : Uri)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showShareLink(link : String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showSnackBarMessage(message : String, icon : Int)
    }

    interface Presenter : BaseBottomSheetContract.Presenter {
        fun shareImageClick(context: Context,image: Bitmap)
        fun shareLinkClick(text : String)
        fun saveImageToGalleryClick(context: Context, image: Bitmap)
    }

}