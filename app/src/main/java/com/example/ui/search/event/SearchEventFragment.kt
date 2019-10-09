package com.example.ui.search.event

import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Event
import com.example.data.models.SearchFilter
import com.example.holders.EventItem
import com.example.ui.search.SearchFragment
import com.example.util.ARG_EVENT
import com.xwray.groupie.kotlinandroidextensions.Item
import javax.inject.Inject
import javax.inject.Provider

class SearchEventFragment : SearchFragment<SearchEventPresenter, Event, SearchFilter.Event>(), SearchEventContract.View {

    @InjectPresenter
    override lateinit var presenter: SearchEventPresenter

    @Inject
    lateinit var presenterProvider: Provider<SearchEventPresenter>

    @ProvidePresenter
    fun providePresenter(): SearchEventPresenter = presenterProvider.get()

    override fun showAboutEvent(event: Event) {
        findNavController().navigate(R.id.about_event, bundleOf(ARG_EVENT to event))
    }

    override fun showEventRequest(event: Event) {
        findNavController().navigate(R.id.request_fragment, bundleOf(ARG_EVENT to event))
    }

    override fun createItem(itemData: Event): Item {
        return EventItem(
                itemData,
                { presenter.onEventClick(itemData) },
                { presenter.onGoToEventClick(itemData) }
        )
    }

    override fun createFilterView(filter: SearchFilter.Event): View {
        return View(requireContext())
    }

    override fun clearFilterView(filterView: View) {

    }
}