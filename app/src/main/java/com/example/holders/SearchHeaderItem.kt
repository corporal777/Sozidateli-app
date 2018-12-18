package com.example.holders

import android.app.DatePickerDialog
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.R
import com.example.ui.search.SearchContract
import com.example.ui.search.SearchHolder
import com.example.util.TYPE_DATE
import com.example.util.TYPE_DATE_PERIOD_FROM
import com.example.util.TYPE_DATE_PERIOD_TO
import com.example.util.Utils
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.brown_button.view.*
import kotlinx.android.synthetic.main.item_search_header.view.*
import java.util.*

open class SearchHeaderItem(private val presenter: SearchContract.Presenter) : Item() {


    private var placesAdapter = GroupAdapter<com.xwray.groupie.ViewHolder>()
    private var typeEventsAdapter = GroupAdapter<com.xwray.groupie.ViewHolder>()
    private lateinit var datePickerDialogDate: DatePickerDialog

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

            datePickerDialogDate = DatePickerDialog(context!!, DatePickerDialog.OnDateSetListener { datePicker, year, monthOfYear, dayOfMonth ->

            }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
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
                    tvDate.text = Utils.defaultDataFormatter.format(searchHolder.date)
                }

                if (searchHolder.dateFrom != 0L) {
                    tvPeriodFrom.text = Utils.defaultDataFormatter.format(searchHolder.dateFrom)
                }

                if (searchHolder.dateTo != 0L) {
                    tvPeriodTo.text = Utils.defaultDataFormatter.format(searchHolder.dateTo)
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
            datePickerDialogDate = DatePickerDialog(it.itemView.context, DatePickerDialog.OnDateSetListener { datePicker, year, monthOfYear, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                presenter.onDateSelected(calendar.timeInMillis, type)

            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

            datePickerDialogDate.show()
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