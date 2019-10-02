package com.example.holders

import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.R
import com.example.extensions.defaultDateFormatter
import com.example.ui.search.SearchContract
import com.example.ui.search.SearchHolder
import com.example.util.SimpleTextWatcher
import com.example.util.TYPE_DATE_PERIOD_FROM
import com.example.util.TYPE_DATE_PERIOD_TO
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_search_header.view.*
import java.util.*

open class SearchHeaderItem(
        private val fragmentManager: FragmentManager,
        private val presenter: SearchContract.Presenter
) : Item() {

    private var placesAdapter = GroupAdapter<GroupieViewHolder>()
    private var typeEventsAdapter = GroupAdapter<GroupieViewHolder>()

    private var searchHolder: SearchHolder? = null
    private var isShowResultLabel = false
    private var isShowEmptyResult = false

    private var simpleTextWatcher = SimpleTextWatcher().setAfterTextChangeRunnable {
        presenter.onSearchTextChange(it.toString())
    }


    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.apply {
            etSearchText.removeTextChangedListener(simpleTextWatcher)
            if (searchHolder != null) {
                searchHolder?.text?.let {
                    etSearchText.setText(it)
                    etSearchText.setSelection(it.length)
                }
                /*if (searchHolder?.date != 0L) {
                    tvDate.text = defaultDateFormatter.format(searchHolder?.date)
                }*/

                if (searchHolder?.dateFrom != 0L) {
                    tvPeriodFrom.text = defaultDateFormatter.format(searchHolder?.dateFrom)
                } else {
                    tvPeriodFrom.text = ""
                }

                if (searchHolder?.dateTo != 0L) {
                    tvPeriodTo.text = defaultDateFormatter.format(searchHolder?.dateTo)
                } else {
                    tvPeriodTo.text = ""
                }

                if (isShowResultLabel) {
                    llSearchResultTitle.visibility = View.VISIBLE
                } else {
                    llSearchResultTitle.visibility = View.GONE
                }
                llSearchEmpty.visibility = if (isShowEmptyResult) View.VISIBLE else View.GONE
                tvSearchResultCount.visibility = if (searchHolder?.totalCountSearchResult == null) View.GONE else View.VISIBLE
                tvSearchResultCount.text = searchHolder?.totalCountSearchResult.let { if (it == null) "0" else it.toString() }
            }

            btnQr.setOnClickListener { presenter.onQrScanClick() }

            etSearchText.addTextChangedListener(simpleTextWatcher)

            recyclerViewPlaces.apply {
                layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                adapter = placesAdapter
            }

            recyclerViewType.apply {
                layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                adapter = typeEventsAdapter
            }

            tvPlaces.setOnClickListener {
                presenter.onOrganizationClick()
            }

            tvTypeEvents.setOnClickListener {
                presenter.onCategoryClick()
            }


            //tvDate.setOnClickListener { presenter.onClickDate(TYPE_DATE) }
            tvPeriodFrom.setOnClickListener { presenter.onClickDate(TYPE_DATE_PERIOD_FROM) }
            tvPeriodTo.setOnClickListener { presenter.onClickDate(TYPE_DATE_PERIOD_TO) }
        }
    }

    fun setSearchData(searchHolder: SearchHolder) {
        this.searchHolder = searchHolder
    }

    fun updatePlacesList(searchHolder: SearchHolder) {
        placesAdapter.update(searchHolder.organizations.map {
            SelectedSearchTypeItem(it) { type ->
                presenter.removeOrganizationItem(type)
            }
        })
    }

    fun updateTypeEventsList(searchHolder: SearchHolder) {
        typeEventsAdapter.update(searchHolder.categories.map {
            SelectedSearchTypeItem(it) { type ->
                presenter.removeCategoryItem(type)
            }
        })
    }

    fun showEmptyResult(isShow: Boolean) {
        isShowEmptyResult = isShow
    }

    fun showDateDialog(date: Long, type: String) {
        val calendar = Calendar.getInstance()
        if (date != 0L) {
            calendar.timeInMillis = date
        }
        val dateDialog = DatePickerDialog.newInstance({ _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            presenter.onDateSelected(calendar.timeInMillis, type)
        }, calendar)

        searchHolder?.dateFrom?.let {
            if (type == TYPE_DATE_PERIOD_TO) {
                val calendarMin = Calendar.getInstance()
                calendarMin.timeInMillis = it
                dateDialog.minDate = calendarMin
            }
        }
        dateDialog.show(fragmentManager, type)

    }

    fun showResultHeader(isShow: Boolean, totalCount: Int? = null) {
        isShowResultLabel = isShow
        this.searchHolder?.totalCountSearchResult = totalCount
    }

    override fun getLayout() = R.layout.item_search_header
}