package com.example.ui.favoritesTab.users

import com.example.data.AppData
import com.example.data.models.UserDetail
import com.example.data.models.UsersFavoriteModel
import com.example.extensions.buildFlow
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.paginationNew.PagingDataSourceFactory
import com.example.util.paginationNew.applyErrorHandler
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import withTimeOut
import javax.inject.Inject

@InjectViewState
class FavoriteUsersPresenter
@Inject constructor(
    private val appData: AppData,
    private val userRepository: UserRepository,
    private val eventRepository: EventRepository
) : BasePresenter<FavoriteUsersContract.View>(appData), FavoriteUsersContract.Presenter {


    private val pagination = PagingDataSourceFactory { limit, offset ->
        val data = mutableMapOf<String, Any>().apply {
            put(UsersFavoriteModel.USERS_FAVORITE_LIMIT, limit)
            put(UsersFavoriteModel.USERS_FAVORITE_OFFSET, offset)
            put(UsersFavoriteModel.USERS_FAVORITE_TYPE, UsersFavoriteModel.USERS_TYPE)
            put(UsersFavoriteModel.USERS_FAVORITE_LOAD_MODEL, true)
            put(UsersFavoriteModel.USERS_FAVORITE_USER, appData.getId())
        }
        userRepository.getUsersFavoritesList(data)
    }.applyErrorHandler { onReceivePagingError(it) }.buildFlow(initialSize = 30, distance = 5)


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += Flowable.create(pagination, BackpressureStrategy.LATEST)
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { it.printStackTrace() },
                onNext = { viewState.setData(it) }
            )
    }


    override fun onUserRemoveFavoritesClick(user: UserDetail) {
        compositeDisposable += eventRepository.deleteFromFavorites(user.binds?.userFavorite?.id.toString())
            .withTimeOut(5000)
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    onReceiveError(it)
                    viewState.updateUser(user)
                },
                onComplete = {
                    viewState.showRemovedFromFavoriteDialog()
                    pagination.invalidateStart()
                })
    }

    override fun onUserClick(user: UserDetail) = viewState.showUser(user)

    override fun onRefreshRequest() = pagination.invalidate()
}
