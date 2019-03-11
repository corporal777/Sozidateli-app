package com.example.ui.profile.profileEdit

import android.net.Uri
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.ProfileField
import com.example.data.models.ProfileFieldExpand
import com.example.data.models.user.User
import com.example.ui.base.BaseContract
import com.example.ui.base.takePhoto.TakePhotoContract
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder

interface ProfileEditContract {
    interface View : TakePhotoContract.View{
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setUser(user: User)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showChangePasswordDialog()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showPdfSelector()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun updateExpandFieldByName(name:String, array:ArrayList<*>?)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showRequiredError(fieldName:String)
    }

    interface Presenter : BaseContract.Presenter{
        fun onSaveClick(fields:List<ProfileField>, expandFields:List<ProfileFieldExpand>)
        fun onChangePasswordShowDialogClick()
        fun onChangePasswordClick(oldPassword:String,newPassword:String)
        fun onUploadDocumentClick()
        fun onPdfSelected(path:String)
    }
}
