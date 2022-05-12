package com.example.ui.state

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.ui.base.BasePresenter
import com.example.ui.users.favorite.FavoriteUsersContract
import javax.inject.Inject

@InjectViewState
class UserStatePresenter
@Inject constructor(
        private val appData: AppData
): BasePresenter<UserStateContract.View>(appData), UserStateContract.Presenter {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

    }

    override fun attachView(view: UserStateContract.View?) {
        super.attachView(view)
        viewState.setStatesUI(arrayListOf(StateItemModel(UserState.BASE, appData.hasBaseState), StateItemModel(UserState.MAX, appData.hasMaxState)))
    }

    override fun onClickClose() {
        viewState.navigateUp()
    }
}