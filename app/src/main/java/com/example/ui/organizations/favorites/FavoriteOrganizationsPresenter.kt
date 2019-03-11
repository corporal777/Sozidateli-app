package com.example.ui.organizations.favorites

import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.models.Organization
import com.example.repository.OrganizationRepository
import com.example.ui.organizations.OrganizationsPresenter
import com.example.util.pagination.PaginationResponse
import io.reactivex.Maybe
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class FavoriteOrganizationsPresenter
@Inject constructor(
        private val organizationRepository: OrganizationRepository
) : OrganizationsPresenter(), FavoriteOrganizationsContract.Presenter {

    override fun onFavoriteChangeClick(organization: Organization) {
        organizationRepository.removeFromFavorite(organization.id)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({ pagination.invalidate() }, { it.printStackTrace() })
                .call(compositeDisposable)
    }

    override fun loadOrganizations(limit: Int, offset: Int): Maybe<PaginationResponse<Organization>> {
        return organizationRepository.favoriteList(limit, offset)
    }
}
