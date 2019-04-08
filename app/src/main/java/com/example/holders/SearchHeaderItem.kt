package com.example.holders

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.R
import com.example.extensions.defaultDateFormatter
import com.example.ui.search.SearchContract
import com.example.ui.search.SearchHolder
import com.example.util.TYPE_DATE
import com.example.util.TYPE_DATE_PERIOD_FROM
import com.example.util.TYPE_DATE_PERIOD_TO
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_search_header.view.*
import java.util.*

open class SearchHeaderItem(
        private val fragmentManager: FragmentManager,
        private val presenter: SearchContract.Presenter
) : Item() {

    private var placesAdapter = GroupAdapter<com.xwray.groupie.ViewHolder>()
    private var typeEventsAdapter = GroupAdapter<com.xwray.groupie.ViewHolder>()

    private var viewHolder: ViewHolder? = null

    override fun bind(viewHolder: ViewHolder, position: Int) {
        this.viewHolder = viewHolder
        viewHolder.itemView.apply {

            btnQr.setOnClickListener { presenter.onQrScanClick() }

            etSearchText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                    presenter.onSearchTextChange(p0.toString())
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            })

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
        }
    }

    fun setSearchData(searchHolder: SearchHolder) {
        viewHolder?.let {
            it.itemView.apply {
                searchHolder.text?.let {
                    etSearchText.setText(it)
                    etSearchText.setSelection(it.length)
                }
                if (searchHolder.date != 0L) {
                    tvDate.text = defaultDateFormatter.format(searchHolder.date)
                }

                if (searchHolder.dateFrom != 0L) {
                    tvPeriodFrom.text = defaultDateFormatter.format(searchHolder.dateFrom)
                }

                if (searchHolder.dateTo != 0L) {
                    tvPeriodTo.text = defaultDateFormatter.format(searchHolder.dateTo)
                }
            }
        }

    }

    fun updatePlacesList(searchHolder: SearchHolder) {
        placesAdapter.update(searchHolder.places.map {
            SelectedSearchTypeItem(it) { type ->
                presenter.removePlacesItem(type)
            }
        })
    }

    fun updateTypeEventsList(searchHolder: SearchHolder) {
        typeEventsAdapter.update(searchHolder.typeEvents.map {
            SelectedSearchTypeItem(it) { type ->
                presenter.removeTypeEventItem(type)
            }
        })
    }


    fun showDateDialog(date: Long, type: String) {
        val calendar = Calendar.getInstance()
        if (date != 0L) {
            calendar.timeInMillis = date
        }
        viewHolder?.let {
            DatePickerDialog.newInstance({ _, year, monthOfYear, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                presenter.onDateSelected(calendar.timeInMillis, type)
            }, calendar)
                    .show(fragmentManager, type)
        }
    }

    fun showResultHeader(isShow: Boolean) {
        viewHolder?.let {
            it.itemView.apply {
                if (isShow) {
                    llSearchResultTitle.visibility = View.VISIBLE
                } else {
                    llSearchResultTitle.visibility = View.GONE
                }
            }

        }

    }

    override fun getLayout() = R.layout.item_search_header
}