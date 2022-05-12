package com.example.ui.organizations.members

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.FavoriteModel
import com.example.data.models.OrganizationMember
import com.example.data.models.OrganizationNewMemberModel
import com.example.extensions.buildList
import com.example.repository.OrganizationRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.PaginationDataSourceFactory
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class OrganizationMembersPresenter
@Inject constructor(
        private val organizationRepository: OrganizationRepository,
        appData: AppData
) : BasePresenter<OrganizationMembersContract.View>(appData), OrganizationMembersContract.Presenter {

    lateinit var organizationId: String

    private var scrollPosition = 0
    private var scrollOffset = 0

    //OrganizationMember

    val pagination = PaginationDataSourceFactory { limit, offset ->
        organizationRepository.getOrganizationMembers(
                mutableMapOf<String, Any>().apply {
                    put(OrganizationMember.MEMBERS_LIMIT, limit)
                    put(OrganizationMember.MEMBERS_OFFSET, offset)
                    put(OrganizationMember.MEMBERS_BINDS, "user")
                    put(OrganizationMember.MEMBERS_ORGANIZATION, organizationId)
                })
    }.buildList()
    /*val pagination = PaginationDataSourceFactory { limit, offset ->
        organizationRepository.getMembers(limit, offset, organizationId)
    }.buildList()*/

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += Observable.create(pagination)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    viewState.setData(it)
                }, {
                    it.printStackTrace()
                })
    }

    override fun onMemberClick(member: /*OrganizationMember*/OrganizationNewMemberModel) {
        viewState.showUser(member.user?.toString()?: "")
    }

    override fun onScrollChange(position: Int, offset: Int) {
        scrollPosition = position
        scrollOffset = offset
    }

    override fun onItemTake(position: Int) {
        pagination.onItemTake(position)
    }

    override fun onRefreshRequest() {
        pagination.invalidate()
    }
}