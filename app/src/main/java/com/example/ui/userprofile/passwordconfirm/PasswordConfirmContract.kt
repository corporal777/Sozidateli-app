package com.example.ui.userprofile.passwordconfirm

import com.example.ui.base.BaseContract

interface PasswordConfirmContract {
    interface View : BaseContract.View {
        fun showConfirmPasswordError()
        fun showPhoneConfirm(phone: String, password: String)
    }

    interface Presenter : BaseContract.Presenter {
        fun onClickConfirmPassword(password: String)
    }
}
