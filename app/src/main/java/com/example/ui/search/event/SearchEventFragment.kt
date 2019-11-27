package com.example.ui.search.event

import android.annotation.SuppressLint
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Event
import com.example.data.models.Event.Companion.FILTER_REGISTRATION_APPROVED
import com.example.data.models.Event.Companion.FILTER_REGISTRATION_DECLINED
import com.example.data.models.Event.Companion.FILTER_REGISTRATION_NOT_REGISTERED
import com.example.data.models.Event.Companion.FILTER_REGISTRATION_PENDING
import com.example.data.models.SearchFilter
import com.example.holders.PlaceholderItem
import com.example.holders.SearchEventItem
import com.example.ui.event.about.AboutEventFragmentArgs
import com.example.ui.search.SearchFragment
import com.xwray.groupie.kotlinandroidextensions.Item
import initDropDownView
import kotlinx.android.synthetic.main.layout_filter_event.view.*
import onTextChanged
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
        findNavController().navigate(R.id.about_event_fragment, AboutEventFragmentArgs.Builder(event.id).build().toBundle())
    }

    override fun createItem(itemData: Event?): Item {
        return if (itemData == null) PlaceholderItem(PlaceholderItem.Type.SEARCH_EVENT)
        else SearchEventItem(
                itemData
        ) { presenter.onEventClick(itemData) }
    }

    @SuppressLint("InflateParams")
    override fun createFilterView(filter: SearchFilter.Event): View {
        return layoutInflater.inflate(R.layout.layout_filter_event, null).apply {
            etAddress.apply {
                setTextWithoutSearch(filter.address)
                onTextChanged { filter.address = it.toString() }
            }
            initTextFilter(etName, filter.name) { filter.name = it }
            initDateFilter(etStart, tilStart, filter.dateStart) { filter.dateStart = it }
            initDateFilter(etFinish, tilFinish, filter.dateFinish) { filter.dateFinish = it }

            val registrations = resources.getStringArray(R.array.registration_status)
            val value = when (filter.registration) {
                FILTER_REGISTRATION_PENDING -> registrations[0]
                FILTER_REGISTRATION_APPROVED -> registrations[1]
                FILTER_REGISTRATION_DECLINED -> registrations[2]
                FILTER_REGISTRATION_NOT_REGISTERED -> registrations[3]
                else -> null
            }
            initDropDownView(
                    tvSubscription,
                    registrations.toList(),
                    value,
                    filterNotChosenVariant,
                    findValue = {
                        when (registrations.indexOf(it)) {
                            0 -> FILTER_REGISTRATION_PENDING
                            1 -> FILTER_REGISTRATION_APPROVED
                            2 -> FILTER_REGISTRATION_DECLINED
                            3 -> FILTER_REGISTRATION_NOT_REGISTERED
                            else -> null
                        }
                    },
                    onVariantChange = { filter.registration = it }
            )

            val interests = filter.interests
            if (interests.isNullOrEmpty()) {
                llTheme.isVisible = false
                llSpec.isVisible = false
            } else {
                initInterests(interests, tvTheme, tilSpec, tvSpec, filter.theme, filter.spec) { theme, spec ->
                    filter.theme = theme
                    filter.spec = spec
                }
                llTheme.isVisible = true
                llSpec.isVisible = true
            }

            val formats = filter.formats
            if (formats.isNullOrEmpty()) {
                llFormat.isVisible = false
            } else {
                llFormat.isVisible = true
                initDropDownView(
                        tvFormat,
                        formats,
                        formats.find { it.id == filter.format }?.name,
                        filterNotChosenVariant,
                        { it.name },
                        { it?.id },
                        { filter.format = it }
                )
            }
        }
    }


    override fun clearFilterView(filterView: View) {
        filterView.apply {
            etAddress.text = null
            etName.text = null
            etStart.text = null
            etFinish.text = null
            tvSubscription.setText(filterNotChosenVariant)
            tvTheme.setText(filterNotChosenVariant)
            tvSpec.setText(filterNotChosenVariant)
            tvFormat.setText(filterNotChosenVariant)
        }
    }
}