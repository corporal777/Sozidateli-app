package com.example.ui.state.maxNew.contacts

import com.example.data.models.UserDetail
import com.example.ui.state.maxNew.base.BaseMaxStateContract
import moxy.viewstate.strategy.alias.OneExecution

interface MaxStatusContactsContract {
    interface View : BaseMaxStateContract.View {
        @OneExecution
        fun setPersonalData(user: UserDetail)
    }
    interface Presenter : BaseMaxStateContract.Presenter {
        fun saveContactsClick(data: MutableMap<String, Any?>)
    }
}