package com.example.ui.organizations.favorites

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Organization
import com.example.ui.organizations.OrganizationsFragment
import javax.inject.Inject
import javax.inject.Provider

class FavoriteOrganizationsFragment : OrganizationsFragment<FavoriteOrganizationsPresenter>(), FavoriteOrganizationsContract.View {

    @InjectPresenter
    override lateinit var presenter: FavoriteOrganizationsPresenter

    @Inject
    lateinit var presenterProvider: Provider<FavoriteOrganizationsPresenter>

    @ProvidePresenter
    fun providePresenter(): FavoriteOrganizationsPresenter = presenterProvider.get()

    private val removeText by lazy { getString(R.string.remove) }

    override fun onItemActionClick(organization: Organization) {
        presenter.onFavoriteChangeClick(organization)
    }

    override fun getActionText(organization: Organization): String = removeText
}
