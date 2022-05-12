package com.example.ui.organizations.events

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.interfaces.ToolbarFragment
import com.example.ui.event.list.EventListFragment
import javax.inject.Inject
import javax.inject.Provider

class OrganizationEventsFragment : EventListFragment<OrganizationEventsPresenter>(), OrganizationEventsContract.View, ToolbarFragment {

    override val title : CharSequence
        get() = requireContext().resources.getString(R.string.organization_events)

    @InjectPresenter
    override lateinit var presenter: OrganizationEventsPresenter

    @Inject
    lateinit var presenterProvider: Provider<OrganizationEventsPresenter>

    @ProvidePresenter
    fun providePresenter(): OrganizationEventsPresenter = presenterProvider.get().apply {
        organizationId = OrganizationEventsFragmentArgs.fromBundle(requireArguments()).organizationId
    }
}
