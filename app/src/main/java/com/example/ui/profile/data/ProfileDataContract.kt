package com.example.ui.profile.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.ViewGroup
import com.example.ui.base.bottomSheet.BaseBSContract
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface ProfileDataContract {
    interface View : BaseBSContract.View{

        @OneExecution
        fun setImage(image : Bitmap)

        @OneExecution
        fun setName(userName : String, userLink : String)

        @Skip
        fun showShareImage(uri : Uri)

        @Skip
        fun showShareLink(link : String)

        @Skip
        fun showSnackBarMessage(message : Int, icon : Int)
    }

    interface Presenter : BaseBSContract.Presenter {
        fun onShareImageClick(context: Context,image: Bitmap)
        fun onShareLinkClick(text : String)
        fun onSaveImageClick(image: Bitmap)
    }

}