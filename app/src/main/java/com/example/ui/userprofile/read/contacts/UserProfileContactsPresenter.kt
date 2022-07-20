package com.example.ui.userprofile.read.contacts

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.ui.userprofile.base.BaseUserProfilePresenter
import javax.inject.Inject

@InjectViewState
class UserProfileContactsPresenter @Inject constructor(
        appData: AppData
) : BaseUserProfilePresenter<UserProfileContactsContract.View>(appData), UserProfileContactsContract.Presenter {
    override fun onEditClick() = viewState.showEdit()

    private var mDy = 0

    override fun attachView(view: UserProfileContactsContract.View?) {
        super.attachView(view)
        viewState.setAppBarElevation(Math.abs(mDy / 10f))
    }

    override fun changeAppBarElevation(value: Int) {
        mDy += value
        viewState.setAppBarElevation(Math.abs(mDy / 10f))
    }
}
