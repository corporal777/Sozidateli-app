package com.example.ui.organizations.events

import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.ui.event.list.EventListFragment
import com.example.ui.views.toolbar.SimpleTitleToolbar
import javax.inject.Inject
import javax.inject.Provider

class OrganizationEventsFragment : EventListFragment<OrganizationEventsPresenter>(), OrganizationEventsContract.View, SimpleTitleToolbar {


    @InjectPresenter
    override lateinit var presenter: OrganizationEventsPresenter

    @Inject
    lateinit var presenterProvider: Provider<OrganizationEventsPresenter>

    @ProvidePresenter
    fun providePresenter(): OrganizationEventsPresenter = presenterProvider.get().apply {
        organizationId = OrganizationEventsFragmentArgs.fromBundle(requireArguments()).organizationId
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbarTitle(getString(R.string.organization_events))
    }
}
