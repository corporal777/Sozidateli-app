package com.example.ui.users.favorite

import com.arellomobile.mvp.InjectViewState
import com.example.data.models.user.User
import com.example.extensions.buildList
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.PaginationDataSourceFactory
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class FavoriteUsersPresenter
@Inject constructor(
        private val userRepository: UserRepository
) : BasePresenter<FavoriteUsersContract.View>(), FavoriteUsersContract.Presenter {

    private val pagination = PaginationDataSourceFactory { limit, offset -> userRepository.getFavoriteUsers(limit, offset) }
            .buildList()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += Observable.create(pagination)
                .withLoadingDialog(viewState)
                .subscribe({ viewState.setData(it) }, { it.printStackTrace() })
    }

    override fun onUserClick(user: User) = viewState.showUser(user)

    override fun onUserRemoveFromFavoritesClick(user: User) {
        val id = user.user_id.toString()
        compositeDisposable += userRepository.removeFromFavorite(id)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({ pagination.invalidate() }, { pagination.invalidate() })
    }

    override fun onItemTake(position: Int) {
        pagination.onItemTake(position)
    }
}
