package com.example.ui.search

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import android.view.View
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.DataArgsSearchType
import com.example.holders.SelectedSearchTypeItem
import com.example.ui.base.BaseFragment
import com.example.util.TYPE_DATE
import com.example.util.TYPE_DATE_PERIOD_FROM
import com.example.util.TYPE_DATE_PERIOD_TO
import com.example.util.Utils
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_search.*
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

class SearchFragment : BaseFragment(), SearchContract.View {

    @InjectPresenter
    lateinit var presenter: SearchPresenter

    @Inject
    lateinit var presenterProvider: Provider<SearchPresenter>

    @ProvidePresenter
    fun providePresenter(): SearchPresenter = presenterProvider.get()

    private var placesAdapter = GroupAdapter<ViewHolder>()
    private var typeEventsAdapter = GroupAdapter<ViewHolder>()
    private lateinit var datePickerDialogDate: DatePickerDialog


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewPlaces.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = placesAdapter
        }

        recyclerViewType.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = typeEventsAdapter
        }

        tvPlaces.setOnClickListener {
            presenter.onPlacesClick()
        }

        tvTypeEvents.setOnClickListener {
            presenter.onTypeEventsClick()
        }

        tvDate.setOnClickListener { presenter.onClickDate(TYPE_DATE) }
        tvPeriodFrom.setOnClickListener { presenter.onClickDate(TYPE_DATE_PERIOD_FROM) }
        tvPeriodTo.setOnClickListener { presenter.onClickDate(TYPE_DATE_PERIOD_TO) }

        datePickerDialogDate = DatePickerDialog(context!!, DatePickerDialog.OnDateSetListener { datePicker, year, monthOfYear, dayOfMonth ->

        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
    }

    override fun showTypeEvent(data: DataArgsSearchType) {
        findNavController().navigate(SearchFragmentDirections.searchToSearchType(data))
    }

    override fun showPlaces(data: DataArgsSearchType) {
        findNavController().navigate(SearchFragmentDirections.searchToSearchType(data))
    }

    override fun setSearchData(searchHolder: SearchHolder) {
        searchHolder.text?.let {
            etSearchText.setText(it)
            etSearchText.setSelection(it.length)
        }
        if (searchHolder.date != 0L) {
            tvDate.text = Utils.defaultDataFormatter.format(searchHolder.date)
        }

        if (searchHolder.dateFrom != 0L) {
            tvPeriodFrom.text = Utils.defaultDataFormatter.format(searchHolder.dateFrom)
        }

        if (searchHolder.dateTo != 0L) {
            tvPeriodTo.text = Utils.defaultDataFormatter.format(searchHolder.dateTo)
        }

    }

    override fun updatePlacesList(searchHolder: SearchHolder) {
        placesAdapter.update(searchHolder.places.map {
            SelectedSearchTypeItem(it, { type ->
                presenter.removePlacesItem(type)
            })
        })
    }

    override fun updateTypeEventsList(searchHolder: SearchHolder) {
        typeEventsAdapter.update(searchHolder.typeEvents.map {
            SelectedSearchTypeItem(it, { type ->
                presenter.removeTypeEventItem(type)
            })
        })
    }

    override fun showSearchResult() {

    }

    override fun showDateDialog(date: Long, type: String) {
        val calendar = Calendar.getInstance()
        if (date != 0L) {
            calendar.timeInMillis = date
        }
        datePickerDialogDate = DatePickerDialog(context!!, DatePickerDialog.OnDateSetListener { datePicker, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            presenter.onDateSelected(calendar.timeInMillis, type)

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        datePickerDialogDate.show()
    }

    override fun isShowToolbar() = true

    override fun layout() = R.layout.fragment_search
}
