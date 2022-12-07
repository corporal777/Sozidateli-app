package com.example.ui.main.inApp

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.Notification
import com.example.repository.UserRepository
import com.example.ui.base.bottomSheet.BaseBottomSheetPresenter
import com.example.ui.profile.data.ProfileDataContract
import com.tbruyelle.rxpermissions2.RxPermissions
import javax.inject.Inject

@InjectViewState
class InAppNotificationPresenter
@Inject constructor(
    private val userRepository: UserRepository,
    private val rxPermissions: RxPermissions,
    private val appData: AppData,
) : BaseBottomSheetPresenter<InAppNotificationContract.View>(appData), InAppNotificationContract.Presenter {

    lateinit var notification : Notification

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setNotification(notification)
    }

}