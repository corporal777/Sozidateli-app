package com.example.ui.userprofile.read.education

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.ui.userprofile.base.BaseUserProfilePresenter
import javax.inject.Inject
import kotlin.math.abs

@InjectViewState
class UserProfileEducationPresenter @Inject constructor(
        appData: AppData
) : BaseUserProfilePresenter<UserProfileEducationContract.View>(appData), UserProfileEducationContract.Presenter {

    override fun onEditClick() = viewState.showEdit()

    private var mDy = 0

    override fun attachView(view: UserProfileEducationContract.View?) {
        super.attachView(view)
        viewState.setAppBarElevation(abs(mDy / 10f))
    }


    override fun changeAppBarElevation(value: Int) {
        mDy += value
        viewState.setAppBarElevation(abs(mDy / 10f))
    }
}
