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
        fun showPhoneConfirm(phone: String)

        @StateStrategyType(SkipStrategy::class)
        fun showEmailConfirm(email: String)

        @StateStrategyType(SkipStrategy::class)
        fun showChangeEmailComplete(email: String)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun photoUpdated(photo: ImageModel)

        @StateStrategyType(SkipStrategy::class)
        fun showPhoneNotUnique(phone: String)

        @StateStrategyType(SkipStrategy::class)
        fun showEmailNotUnique(email : String)

        @StateStrategyType(SkipStrategy::class)
        fun updatePhoneConfirmation(phone : String)

        @StateStrategyType(SkipStrategy::class)
        fun showCheckPassword(phone: String?)

        @StateStrategyType(SkipStrategy::class)
        fun hideCheckPassword()
    }
    interface Presenter : BaseContract.Presenter  {
        fun onClickClose()
        fun updateFiles(data: MutableMap<String, Any?>)

        fun checkEmailIsUnique(email: String)
        fun checkPhoneIsUnique(phone: String)

        fun onShowEmailConfirm(email: String)
        fun onShowPhoneConfirm(phone: String)


        fun onTakePhotoFromGalleryClick()
        fun onTakePhotoFromCameraClick()
        fun onRemovePhotoClick()

        fun checkPassword(password: String, phone: String)
    }
}