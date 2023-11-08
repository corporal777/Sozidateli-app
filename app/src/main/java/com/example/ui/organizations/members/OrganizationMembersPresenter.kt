package com.example.ui.organizations.members

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.bodies.AddToFavoriteEntityModel
import com.example.data.bodies.AddToFavoriteModel
import com.example.data.models.OrganizationMember
import com.example.data.models.OrganizationMemberModel
import com.example.extensions.buildList
import com.example.repository.EventRepository
import com.example.repository.OrganizationRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.observable.PaginationDataSourceFactory
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import javax.inject.Inject

@InjectViewState
class OrganizationMembersPresenter
@Inject constructor(
    private val organizationRepository: OrganizationRepository,
    private val eventRepository: EventRepository,
    val appData: AppData
) : BasePresenter<OrganizationMembersContract.View>(appData),
    OrganizationMembersContract.Presenter {

    private var isFirstAttach = true
    lateinit var organizationId: String

    val pagination = PaginationDataSourceFactory { limit, offset ->
        organizationRepository.getOrganizationMembers(
            mutableMapOf<String, Any>().apply {
                put(OrganizationMember.MEMBERS_LIMIT, limit)
                put(OrganizationMember.MEMBERS_OFFSET, offset)
                put(OrganizationMember.MEMBERS_BINDS, "user,userFavorite")
                put(OrganizationMember.MEMBERS_ORGANIZATION, organizationId)
            })
    }.buildList(enablePlaceholders = false, initialSize = 30)

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setData(List(10) { null })
        compositeDisposable += Observable.create(pagination)
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onNext = { viewState.setData(it) }
            )
    }


    override fun attachView(view: OrganizationMembersContract.View?) {
        super.attachView(view)
        if (isFirstAttach) isFirstAttach = false
        else pagination.invalidate()
    }

    override fun onAddUserFavoriteCLick(member: OrganizationMemberModel) {
        compositeDisposable += Single.create<Boolean> { emitter ->
            val disposables = CompositeDisposable()
            disposables += if (member.binds?.userFavorite == null) {
                eventRepository.addToFavorites(userFavoriteBody(member.user))
                    .subscribeSimple(
                        onError = { emitter.onError(it) },
                        onSuccess = { emitter.onSuccess(true) })
            } else {
                eventRepository.deleteFromFavorite(member.binds.userFavorite?.id.toString())
                    .subscribeSimple(
                        onError = { emitter.onError(it) },
                        onComplete = { emitter.onSuccess(false) })
            }
            emitter.setDisposable(disposables)
        }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = {
                    pagination.invalidate()
                    viewState.apply {
                        if (it) showEventAddedToFavoriteDialog()
                        else showEventRemovedFromFavoriteDialog()
                    }
                }
            )
    }

    override fun onMemberClick(memberId: Int?) {
        if (appData.isCurrentUser(memberId.toString())) {
            viewState.showCurrentUser(appData.getUser().id.toString())
        } else viewState.showUser(memberId.toString())
    }

    fun isCurrentUser(id: String): Boolean {
        return appData.isCurrentUser(id)
    }

    private fun userFavoriteBody(user: Int?): AddToFavoriteModel {
        return AddToFavoriteModel(
            appData.getId(),
            AddToFavoriteEntityModel(AddToFavoriteEntityModel.FAVORITE_SPEAKER, user)
        )
    }

    override fun onItemTake(position: Int) = pagination.onItemTake(position)
    override fun onRefreshRequest() = pagination.invalidate()
}