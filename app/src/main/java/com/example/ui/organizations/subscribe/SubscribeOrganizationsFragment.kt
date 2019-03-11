package com.example.ui.organizations.subscribe

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Organization
import com.example.ui.organizations.OrganizationsFragment
import javax.inject.Inject
import javax.inject.Provider

class SubscribeOrganizationsFragment : OrganizationsFragment<SubscribeOrganizationsPresenter>(), SubscribeOrganizationsContract.View {

    @InjectPresenter(type = PresenterType.WEAK, tag = "SubscribeOrganizationsFragment")
    override lateinit var presenter: SubscribeOrganizationsPresenter

    @Inject
    lateinit var presenterProvider: Provider<SubscribeOrganizationsPresenter>

    @ProvidePresenter(type = PresenterType.WEAK, tag = "SubscribeOrganizationsFragment")
    fun providePresenter(): SubscribeOrganizationsPresenter = presenterProvider.get()

    private val unsubscribeText by lazy { getString(R.string.unsubscribe) }

    override fun onItemActionClick(organization: Organization) {
        presenter.onSubscribeChangeClick(organization)
    }

    override fun getActionText(organization: Organization) = unsubscribeText
}