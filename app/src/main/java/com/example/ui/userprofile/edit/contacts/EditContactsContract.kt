package com.example.ui.userprofile.edit.contacts

import com.example.data.models.AcademicDegreeModel
import com.example.data.models.EducationModel
import com.example.data.models.FieldDetails
import com.example.data.models.ToggleIntModel
import com.example.data.models.UserDetail
import com.example.data.models.UserEditDataType
import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface EditContactsContract {
    interface View : BaseContract.View {
        @OneExecution
        fun setPlaceholder()

        @OneExecution
        fun setContactsData(user: UserDetail)

        @Skip
        fun showChangeEmail()

        @Skip
        fun showPhoneNotUnique(phone: String, withUpdate : Boolean)

        @OneExecution
        fun updatePhone(phone: FieldDetails?)

        @OneExecution
        fun showPhoneConfirmation(phone: String, withUpdate : Boolean)
    }

    interface Presenter : BaseContract.Presenter {
        fun onChangeEmailClick()
        fun onUpdatePhone()
        fun checkPhoneIsUnique(phone: String, withUpdate : Boolean)
        fun onSaveContactsClick(data: MutableMap<String, Any?>)
    }
}