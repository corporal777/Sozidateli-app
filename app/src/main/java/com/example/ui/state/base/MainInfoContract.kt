package com.example.ui.state.base

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.FieldDetails
import com.example.data.models.FileModel
import com.example.data.models.ImageModel
import com.example.data.models.UserDetail
import com.example.ui.base.BaseContract

interface MainInfoContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setPersonalData(user: UserDetail)

        @StateStrategyType(SkipStrategy::class)
        fun goToNext()

        @StateStrategyType(SkipStrategy::class)
        fun showUpdateError(message: String? = null)

        @StateStrategyType(SkipStrategy::class)
        fun showPhoneConfirm(phone: String)

        @StateStrategyType(SkipStrategy::class)
        fun showChangeEmailComplete(email: String)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun photoUpdated(photo: ImageModel)

        @StateStrategyType(SkipStrategy::class)
        fun showPhoneNotUnique(phone: String)

        @StateStrategyType(SkipStrategy::class)
        fun codeSuccess(phone : List<FieldDetails>?, canGoNext : Boolean)

        @StateStrategyType(SkipStrategy::class)
        fun showCheckPassword(phone: String?)
    }
    interface Presenter : BaseContract.Presenter, BaseContract.OnChangeElevation  {
        fun onClickClose()
        fun updateFiles(data: MutableMap<String, Any?>)
        fun onConfirmPhoneClick(phone: String)
        fun sendEmail(email: String)

        fun onTakePhotoFromGalleryClick()
        fun onTakePhotoFromCameraClick()
        fun onRemovePhotoClick()


        fun checkPassword(password: String, phone: String)
        fun confirmCode(phone: String, code: String)
    }
}