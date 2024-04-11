package com.example.ui.state.base

import com.example.data.models.FieldDetails
import com.example.data.models.ImageModel
import com.example.data.models.NewUserAddress
import com.example.data.models.ToggleStringModel
import com.example.data.models.UserDetail
import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface MainInfoContract {
    interface View : BaseContract.View {
        @OneExecution
        fun setPersonalData(user: UserDetail)

        @Skip
        fun goToNext()

        @OneExecution
        fun showEmailConfirm(email: String)

        @Skip
        fun showChangeImage()

        @OneExecution
        fun showPhoneEdit()

        @OneExecution
        fun showEmailNotUnique(email : String)

        @OneExecution
        fun updateImage(photo: ImageModel?, isDefault : Boolean?)

        @OneExecution
        fun updatePhone(phone: FieldDetails?)
    }
    interface Presenter : BaseContract.Presenter  {
        fun onSaveData(data: MutableMap<String, Any?>)

        fun checkEmailIsUnique(withCheck : Boolean, email: String)

        fun onShowPhoneEdit()
        fun onShowImageEdit()
        fun onUpdateImage(photo: ImageModel?)
    }
}