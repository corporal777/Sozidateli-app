package com.example.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.viewbinding.ViewBinding
import com.example.app.R
import com.example.app.databinding.LayoutFilterBinding
import com.example.app.databinding.LayoutListSearchBinding
import com.example.data.models.InterestNew
import com.example.data.models.SearchFilter
import com.example.extensions.defaultDateFormatter
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.formatToDefaultServerDate
import com.example.extensions.initAsDatePicker
import com.example.extensions.initDropDownView
import com.example.extensions.onTextChanged
import com.example.extensions.updateItem
import com.example.interfaces.SearchInterfaceProvider
import com.example.ui.base.BaseVBFragment
import com.example.ui.event.list.recommendations.items.NoEventItem
import com.example.ui.search.tabs.SearchTabsFragment
import com.example.ui.views.suggestFieldView.region.SearchRegionBottomSheet
import com.example.ui.views.suggestFieldView.town.SearchTownBottomSheet
import com.example.util.DATE_STRING_FORMAT_SHORT_MONTH_FULL_YEAR
import com.example.util.initInput
import com.example.util.pagination.PaginationGroupAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout
import com.xwray.groupie.Group
import com.xwray.groupie.GroupieViewHolder

abstract class SearchFragment<V : ViewBinding, P : SearchContract.Presenter> :
    BaseVBFragment<V>(), SearchContract.View {

    abstract var presenter: P

    override fun onResume() {
        super.onResume()
        (this as? SearchInterfaceProvider)?.apply {
            presenter.onResume(provideSearchInterface())
            return
        }
        (parentFragment as? SearchInterfaceProvider)?.apply {
            presenter.onResume(provideSearchInterface())
            return
        }
    }

    override fun setHasFilter() {
        (parentFragment as? SearchTabsFragment)?.apply {
            setFiltersChosen(this@SearchFragment.presenter.isHasFilter())
        }
    }
}