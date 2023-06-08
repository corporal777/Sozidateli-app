package com.example.ui.users.favorite

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.EventFavoriteModel
import com.example.data.models.UserDetail
import com.example.data.models.UsersFavoriteModel
import com.example.data.models.user.User
import com.example.extensions.buildList
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.observable.PaginationDataSourceFactory
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class FavoriteUsersPresenter
@Inject constructor(
    private val appData: AppData,
    private val userRepository: UserRepository,
    private val eventRepository: EventRepository
) : BasePresenter<FavoriteUsersContract.View>(appData), FavoriteUsersContract.Presenter {

    private var firstLaunch = true

    private val pagination = PaginationDataSourceFactory { limit, offset ->
        val data = mutableMapOf<String, Any>().apply {
            put(UsersFavoriteModel.USERS_FAVORITE_LIMIT, limit)
            put(UsersFavoriteModel.USERS_FAVORITE_OFFSET, offset)
            put(UsersFavoriteModel.USERS_FAVORITE_TYPE, UsersFavoriteModel.USERS_TYPE)
            put(UsersFavoriteModel.USERS_FAVORITE_LOAD_MODEL, true)
            put(UsersFavoriteModel.USERS_FAVORITE_USER, appData.getId())
        }
        userRepository.getUsersFavoritesList(data)
    }
        .buildList(enablePlaceholders = true)

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setData(List(20) { null })
        compositeDisposable += Observable.create(pagination)
            .subscribe({
                val uid = appData.getId()
                it.forEach { user -> user?.isCurrentUser = user?.id == uid }
                viewState.setData(it)
            }, { it.printStackTrace() })
    }

    override fun attachView(view: FavoriteUsersContract.View?) {
        super.attachView(view)
        if (firstLaunch) firstLaunch = false
        else pagination.invalidate()
    }

    override fun onUserClick(user: UserDetail) = viewState.showUser(user)

    override fun onUserRemoveFromFavoritesClick(user: UserDetail) {
        compositeDisposable += eventRepository.deleteFromFavorite(user.binds?.userFavorite?.id.toString())
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    pagination.invalidate()
                },
                onComplete = {
                    viewState.showEventRemovedFromFavoriteDialog()
                    pagination.invalidate()
                })
    }

    override fun onItemTake(position: Int) {
        pagination.onItemTake(position)
    }

    override fun onRefreshRequest() {
        pagination.invalidate()
    }
}
