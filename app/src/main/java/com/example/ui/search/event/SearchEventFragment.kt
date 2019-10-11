package com.example.ui.search.event

import android.view.View
import android.widget.AutoCompleteTextView
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Event
import com.example.data.models.Interest
import com.example.data.models.SearchFilter
import com.example.holders.SearchEventItem
import com.example.ui.search.SearchFragment
import com.example.ui.search.event.SearchEventPresenter.Companion.FILTER_REGISTRATION_APPROVED
import com.example.ui.search.event.SearchEventPresenter.Companion.FILTER_REGISTRATION_DECLINED
import com.example.ui.search.event.SearchEventPresenter.Companion.FILTER_REGISTRATION_NOT_REGISTERED
import com.example.ui.search.event.SearchEventPresenter.Companion.FILTER_REGISTRATION_PENDING
import com.example.util.ARG_EVENT
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.layout_filter_event.view.*
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

    override fun createItem(itemData: Event): Item {
        return SearchEventItem(
                itemData
        ) { presenter.onEventClick(itemData) }
    }

    override fun createFilterView(filter: SearchFilter.Event): View {
        return layoutInflater.inflate(R.layout.layout_filter_event, null).apply {
            initTextFilter(etAddress, filter.address) { filter.address = it }
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
                    {
                        when (registrations.indexOf(it)) {
                            0 -> FILTER_REGISTRATION_PENDING
                            1 -> FILTER_REGISTRATION_APPROVED
                            2 -> FILTER_REGISTRATION_DECLINED
                            3 -> FILTER_REGISTRATION_NOT_REGISTERED
                            else -> null
                        }
                    },
                    { filter.registration = it }
            )

            val interests = filter.interests
            if (!interests.isNullOrEmpty()) {
                val specializations = interests.keys
                val selectedSpecialization = findInterest(filter.specialization, specializations)
                initDropDownView(
                        tvSpecialization,
                        specializations,
                        selectedSpecialization?.value,
                        { it.value },
                        { it?.id },
                        { id ->
                            filter.specialization = id
                            filter.theme = null
                            val spec = findInterest(id, specializations)
                            val themes = spec?.let { interests[it] }
                            initTheme(tilTheme, tvTheme, themes, filter)
                        }
                )

                val themes = selectedSpecialization?.let { interests[it] }
                initTheme(tilTheme, tvTheme, themes, filter)
            }
        }
    }

    private fun initTheme(inputLayout: View, textView: AutoCompleteTextView, interests: List<Interest>?, filter: SearchFilter.Event) {
        if (interests == null) {
            textView.isEnabled = false
            textView.setText(filterNotChosenVariant)
            inputLayout.isEnabled = false
        } else {
            val selectedTheme = findInterest(filter.theme, interests)
            initDropDownView(
                    textView,
                    interests,
                    selectedTheme?.value,
                    { it.value },
                    { it?.id },
                    { filter.theme = it }
            )
            textView.isEnabled = true
            inputLayout.isEnabled = true
        }
    }

    private fun findInterest(id: Int?, interests: Collection<Interest>): Interest? {
        return id?.let { interests.find { it.id == id } }
    }

    override fun clearFilterView(filterView: View) {
        filterView.apply {
            etAddress.text = null
            etName.text = null
            etStart.text = null
            etFinish.text = null
            tvSubscription.setText(filterNotChosenVariant)
            tvSpecialization.setText(filterNotChosenVariant)
            tvTheme.setText(filterNotChosenVariant)
        }
    }
}