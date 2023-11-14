package com.example.ui.partner

import com.example.data.models.PartnerModel
import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution

interface PartnerContract {
    interface View : BaseContract.View {
        @OneExecution
        fun setData(partner: PartnerModel)
    }

    interface Presenter : BaseContract.Presenter {
        fun onRefreshRequest()
    }
}
