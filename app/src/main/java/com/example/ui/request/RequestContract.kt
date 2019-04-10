package com.example.ui.request

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.RegisterFieldResponse
import com.example.ui.base.BaseContract

interface RequestContract {
    interface View : BaseContract.View {

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun enableActionButton(enable: Boolean)

        @StateStrategyType(SkipStrategy::class)
        fun setFields(fieldResponse: RegisterFieldResponse)

        @StateStrategyType(SkipStrategy::class)
        fun openFileSelector()

        @StateStrategyType(SkipStrategy::class)
        fun updateFileField(position: Int,path:String)

        @StateStrategyType(SkipStrategy::class)
        fun showSuccessRegister()
    }

    interface Presenter : BaseContract.Presenter {
        fun onCloseClick()
        fun onDataChange(field:String,value:Any?,fieldForRemove:String?=null)
        fun onRegisterClick()
        fun onClickOpenFileSelector(position:Int)
        fun onFileSelected(path:String)
        fun onGoTeEventListClick()
    }
}
