package com.example.ui.organizations.members

import com.example.data.AppData
import com.example.data.models.EventUserFavorite
import com.example.data.models.Optional
import com.example.data.models.OrganizationMember
import com.example.data.models.UserDetail
import com.example.extensions.buildFlow
import com.example.repository.EventRepository
import com.example.repository.OrganizationRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.PaginationResponse
import com.example.util.paginationNew.PagingDataSourceFactory
import com.example.util.paginationNew.applyErrorHandler
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import withTimeOut
import javax.inject.Inject

@InjectViewState
class OrganizationMembersPresenter
@Inject constructor(
    private val organizationRepository: OrganizationRepository,
    private val eventRepository: EventRepository,
    val appData: AppData
) : BasePresenter<OrganizationMembersContract.View>(appData),
    OrganizationMembersContract.Presenter {

    lateinit var organizationId: String

    private val pagination = PagingDataSourceFactory { limit, offset ->
        organizationRepository.getOrganizationMembers(
            mutableMapOf<String, Any>().apply {
                put(OrganizationMember.MEMBERS_LIMIT, limit)
                put(OrganizationMember.MEMBERS_OFFSET, offset)
                put(OrganizationMember.MEMBERS_BINDS, "user,userFavorite")
                put(OrganizationMember.MEMBERS_ORGANIZATION, organizationId)
            }).map { PaginationResponse(it.totalCount, it.data.mapNotNull { it.binds?.user }) }
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

    override fun onAddUserFavoriteCLick(user: UserDetail) {
        compositeDisposable += Single.defer {
            if (user.binds?.userFavorite != null)
                eventRepository.deleteFromFavorites(user.binds?.userFavorite?.id.toString())
                    .andThen(Single.just(Optional(null)))
            else eventRepository.addUserToFavorites(user.id.toString())
                .map { Optional(EventUserFavorite(it.id, it.user)) }
        }
            .doOnSuccess { user.binds?.userFavorite = it.value }
            .withTimeOut(5000)
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    viewState.updateUser(user)
                    onReceiveError(it)
                },
                onSuccess = {
                    viewState.updateUser(user)
                    if (it.value == null) viewState.showRemovedFromFavoriteDialog()
                    else viewState.showAddedToFavoriteDialog()
                })
    }

    override fun onMemberClick(user: UserDetail) {
        if (appData.isCurrentUser(user.id.toString())) {
            viewState.showCurrentUser(appData.getUser().id.toString())
        } else viewState.showUser(user.id.toString())
    }

    fun isCurrentUser(id: String): Boolean = appData.isCurrentUser(id)

    override fun onRefreshRequest() = pagination.invalidate()
}