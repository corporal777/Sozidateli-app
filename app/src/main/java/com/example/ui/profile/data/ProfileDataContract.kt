package com.example.ui.profile.data

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.example.ui.base.bottomSheet.BaseBottomSheetContract
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution

interface ProfileDataContract {

    interface View : BaseBottomSheetContract.View{

        @AddToEndSingle
        fun setImage(image : Bitmap)

        @AddToEndSingle
        fun setName(userName : String, userLink : String)

        @OneExecution
        fun showShareImage(uri : Uri)

        @OneExecution
        fun showShareLink(link : String)

        @OneExecution
        fun showSnackBarMessage(message : String, icon : Int)
    }

    interface Presenter : BaseBottomSheetContract.Presenter {
        fun shareImageClick(context: Context,image: Bitmap)
        fun shareLinkClick(text : String)
        fun saveImageToGalleryClick(context: Context, image: Bitmap)
    }

}