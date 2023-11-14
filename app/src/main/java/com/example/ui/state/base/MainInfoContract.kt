package com.example.ui.state.base

import com.example.data.models.ImageModel
import com.example.data.models.UserDetail
import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface MainInfoContract {
    interface View : BaseContract.View {
        @AddToEndSingle
        fun setPlaceholder()

        @AddToEndSingle
        fun setPersonalData(user: UserDetail)

        @Skip
        fun goToNext()

        @OneExecution
        fun showEmailConfirm(email: String)

        @OneExecution
        fun showChangeImage()

        @OneExecution
        fun photoUpdated(photo: ImageModel?)

        @OneExecution
        fun showEmailNotUnique(email : String)

        @OneExecution
        fun showPhoneEdit(phone : String?)
    }
    interface Presenter : BaseContract.Presenter  {
        fun onClickClose()
        fun onSaveData(data: MutableMap<String, Any?>)

        fun checkEmailIsUnique(email: String)

        fun onShowEmailConfirm(email: String)
        fun onShowPhoneEdit(phone: String?)
        fun onShowImageEdit()
    }
}