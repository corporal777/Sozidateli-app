package com.example.ui.userprofile.read.maindata

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.ui.userprofile.base.BaseUserProfilePresenter
import javax.inject.Inject
import kotlin.math.abs

@InjectViewState
class UserProfileMainDataPresenter @Inject constructor(
        appData: AppData
) : BaseUserProfilePresenter<UserProfileMainDataContract.View>(appData), UserProfileMainDataContract.Presenter {


    private var mDy = 0

    override fun onEditClick() = viewState.showEdit()


    override fun attachView(view: UserProfileMainDataContract.View?) {
        super.attachView(view)
        viewState.setAppBarElevation(abs(mDy / 10f))
    }

    override fun changeAppBarElevation(value: Int) {
        mDy += value
        viewState.setAppBarElevation(abs(mDy / 10f))
    }
}
