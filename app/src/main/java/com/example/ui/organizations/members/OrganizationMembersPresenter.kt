package com.example.ui.organizations.members

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.OrganizationMember
import com.example.data.models.OrganizationNewMemberModel
import com.example.extensions.buildList
import com.example.repository.OrganizationRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.observable.PaginationDataSourceFactory
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withProgressBarLoadingDialog
import javax.inject.Inject

@InjectViewState
class OrganizationMembersPresenter
@Inject constructor(
    private val organizationRepository: OrganizationRepository,
    val appData: AppData
) : BasePresenter<OrganizationMembersContract.View>(appData),
    OrganizationMembersContract.Presenter {

    lateinit var organizationId: String

    val pagination = PaginationDataSourceFactory { limit, offset ->
        organizationRepository.getOrganizationMembers(
            mutableMapOf<String, Any>().apply {
                put(OrganizationMember.MEMBERS_LIMIT, limit)
                put(OrganizationMember.MEMBERS_OFFSET, offset)
                put(OrganizationMember.MEMBERS_BINDS, "user,userFavorite")
                put(OrganizationMember.MEMBERS_ORGANIZATION, organizationId)
            })
    }.buildList()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += Observable.create(pagination)
            .performOnBackgroundOutOnMain()
            .withProgressBarLoadingDialog(viewState)
            .subscribe({
                viewState.setData(it)
            }, {
                it.printStackTrace()
            })
    }


    override fun attachView(view: OrganizationMembersContract.View?) {
        super.attachView(view)
    }


    override fun onMemberClick(member: OrganizationNewMemberModel) {
        if (appData.isCurrentUser(member.user?.toString() ?: "")) {
            viewState.showCurrentUser(appData.getUserNew().id.toString())
        } else {
            viewState.showUser(member.user?.toString() ?: "")
        }
    }

    override fun onItemTake(position: Int) {
        pagination.onItemTake(position)
    }

    override fun onRefreshRequest() {
        pagination.invalidate()
    }
}