package com.example.ui.organizations.favorites

import com.arellomobile.mvp.InjectViewState
import com.example.data.models.Organization
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
class FavoriteOrganizationsPresenter
@Inject constructor(
        private val organizationRepository: OrganizationRepository
) : BasePresenter<FavoriteOrganizationsContract.View>(), FavoriteOrganizationsContract.Presenter {

    private val pagination = PaginationDataSourceFactory { limit, offset -> organizationRepository.favoriteList(limit, offset) }
            .buildList()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += Observable.create(pagination)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({ viewState.setOrganizations(it) }, { it.printStackTrace() })
    }

    override fun onOrganizationClick(organization: Organization) {
        viewState.showOrganization(organization)
    }

    override fun onRemoveFromFavoriteClick(organization: Organization) {
        compositeDisposable += organizationRepository.removeFromFavorite(organization.id)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({ pagination.invalidate() }, { pagination.invalidate() })
    }

    override fun onItemTake(position: Int) {
        pagination.onItemTake(position)
    }
}
