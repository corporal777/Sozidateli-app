package com.example.ui.state.base

import com.example.data.models.FieldDetails
import com.example.data.models.ImageModel
import com.example.data.models.NewUserAddress
import com.example.data.models.SearchRegion
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
        fun setUserAvatar(user: UserDetail)

        @Skip
        fun goToNext()

        @OneExecution
        fun showEmailConfirm(email: String)

        @OneExecution
        fun showChangePhone()

        @Skip
        fun showChangeImage()

        @Skip
        fun showEmailNotUnique(email : String)

        @Skip
        fun enableBtnSave(isEnable: Boolean)
    }
    interface Presenter : BaseContract.Presenter  {
        fun onSaveData()

        fun checkEmailIsUnique(withCheck : Boolean, email: String)

        fun onUpdateImage()

        fun onChangeBirthday(birthday : String)
        fun onChangeGender(gender : String)
        fun onChangeRegion(region : String)
        fun onChangeCity(city : String)

        fun onChangeShowBirthday(show : Boolean)
        fun onChangeShowGender(show : Boolean)
        fun onChangeShowAddress(show : Boolean)
    }
}