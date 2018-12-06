package com.example.ui.profile.favoritesTab

import com.arellomobile.mvp.InjectViewState
import com.example.repository.ChatRepository
import com.example.ui.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class FavoritePresenter
@Inject constructor(private val chatRepository: ChatRepository
) : BasePresenter<FavoriteContract.View>(), FavoriteContract.Presenter{
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()


    }

    override fun attachView(view: FavoriteContract.View?) {
        super.attachView(view)

    }

    override fun setSelectedTab(position: Int) {
        viewState.selectTab(position)
    }
}
