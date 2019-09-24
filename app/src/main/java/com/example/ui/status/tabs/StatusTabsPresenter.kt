package com.example.ui.status.tabs

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.user.User
import com.example.ui.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class StatusTabsPresenter
@Inject constructor(
        private val appData: AppData
) : BasePresenter<StatusTabsContract.View>(), StatusTabsContract.Presenter {

    private var currentTab = 0

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        when (appData.getUser().user_status) {
            User.Status.LOW_PROTECTION -> viewState.selectTab(0)
            User.Status.MID_PROTECTION -> viewState.selectTab(1)
            User.Status.MAX_PROTECTION -> viewState.selectTab(2)
        }
    }

    override fun onCloseClick() {
        viewState.navigateUp()
    }

    override fun onAnonymousSelected() {
        currentTab = 0
    }

    override fun onProtectedSelected() {
        currentTab = 1
    }

    override fun onMaximumSelected() {
        currentTab = 2
    }
}
