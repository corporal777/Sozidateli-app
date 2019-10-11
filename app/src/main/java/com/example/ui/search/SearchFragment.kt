package com.example.ui.search

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.EditText
import com.example.R
import com.example.adapters.NoFilterArrayAdapter
import com.example.data.models.SearchFilter
import com.example.extensions.defaultDateFormatter
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.formatToDefaultServerDate
import com.example.interfaces.SearchInterfaceProvider
import com.example.ui.base.BaseFragment
import com.example.ui.views.BottomDialog
import com.example.util.DATE_STRING_FORMAT_SHORT_MONTH_FULL_YEAR
import com.example.util.pagination.PaginationListGroupAdapter
import com.google.android.material.textfield.TextInputLayout
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import initAsDatePicker
import kotlinx.android.synthetic.main.fragment_search.*
import onTextChanged

abstract class SearchFragment<P : SearchContract.Presenter<I>, I, F : SearchFilter> : BaseFragment(), SearchContract.View<I, F> {

    abstract var presenter: P

    private var filterDialog: BottomDialog? = null
    private var filterView: View? = null

    protected val filterNotChosenVariant by lazy {
        getString(R.string.search_filters_not_chosen)
    }

    protected val adapter by lazy {
        PaginationListGroupAdapter<GroupieViewHolder>().apply {
            setOnItemTakeCallback(object : PaginationListGroupAdapter.OnItemTakeCallback {
                override fun onItemTake(position: Int) {
                    presenter.onItemTake(position)
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        (parentFragment as? SearchInterfaceProvider)?.apply {
            presenter.onResume(provideSearchInterface())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            adapter = this@SearchFragment.adapter
        }
    }

    override fun setData(data: List<I>) {
        adapter.update(data.map(::createItem))
    }

    override fun showFilter(filter: F) {
        val filterContainer = (layoutInflater.inflate(R.layout.layout_filter, null) as ViewGroup).apply {
            findViewById<ViewGroup>(R.id.flFilters).apply {
                val filterView = createFilterView(filter)
                this@SearchFragment.filterView = filterView
                addView(filterView)
            }

            findViewById<View>(R.id.btnApply).setOnClickListener { presenter.onFilterApplyClick() }
            findViewById<View>(R.id.btnClear).setOnClickListener { presenter.onFilterClearClick() }
            findViewById<View>(R.id.btnClose).setOnClickListener { filterDialog?.dismiss() }
        }

        filterDialog = BottomDialog(requireContext(), filterContainer)
                .apply {
                    setOnDismissListener { presenter.onFilterCancel() }
                    show()
                }
    }

    override fun hideFilter() {
        filterDialog?.apply {
            setOnDismissListener(null)
            dismiss()
        }
    }

    override fun clearFilter() {
        filterView?.let { clearFilterView(it) }
    }

    protected fun initTextFilter(editText: EditText, text: String?, onTextChange: (String?) -> Unit) {
        editText.apply {
            onTextChanged { onTextChange(it?.toString()) }
            setText(text)
        }
    }

    protected fun initDateFilter(editText: EditText, inputLayout: TextInputLayout, date: String?, onDateChange: (String?) -> Unit) {
        val parsedDate = date?.let { defaultServerDateFormatter.parse(it) }
        val formattedDate = parsedDate?.let { defaultDateFormatter.format(it) }
        editText.apply {
            onTextChanged { onDateChange(it?.toString()?.formatToDefaultServerDate()) }
            setText(formattedDate)
        }

        inputLayout.initAsDatePicker(parsedDate) { year, month, day ->
            String.format(DATE_STRING_FORMAT_SHORT_MONTH_FULL_YEAR, day, month + 1, year)
        }
    }

    protected fun <T> initDropDownView(textView: AutoCompleteTextView, variants: Collection<String>, selectedVariant: String?, findValue: (String?) -> T?, onVariantChange: (T?) -> Unit) {
        val variantsMap = linkedMapOf<String, T?>()
        variants.associateWithTo(variantsMap) { findValue(it) }
        initDropDownView(textView, variantsMap, selectedVariant, onVariantChange)
    }

    protected fun <K, V> initDropDownView(textView: AutoCompleteTextView, variants: Collection<K>, selectedVariant: String?, transformKey: (K) -> String, findValue: (K?) -> V?, onVariantChange: (V?) -> Unit) {
        val variantsMap = linkedMapOf<String, V?>()
        variants.associateTo(variantsMap, { transformKey(it) to findValue(it) })
        initDropDownView(textView, variantsMap, selectedVariant, onVariantChange)
    }

    protected fun <T> initDropDownView(textView: AutoCompleteTextView, variants: Map<String, T?>, selectedVariant: String?, onVariantChange: (T?) -> Unit) {
        val fullFilter = variants.plus(filterNotChosenVariant to null)
        textView.apply {
            keyListener = null
            setAdapter(NoFilterArrayAdapter(requireContext(), R.layout.item_dropdown, R.id.tvText, fullFilter.keys.toMutableList()))
            setText(selectedVariant ?: filterNotChosenVariant, false)
            onTextChanged {
                val variant = it.toString()
                onVariantChange(fullFilter[variant])
            }
        }
    }

    protected fun initBiFilter(textView: AutoCompleteTextView, filter: List<String>, boolean: Boolean?, onSubscriptionChange: (Boolean?) -> Unit) {
        val selectedValue = when (boolean) {
            true -> filter[0]
            false -> filter[1]
            else -> null
        }
        initDropDownView(textView, filter.toList(), selectedValue, {
            when (filter.indexOf(it)) {
                0 -> true
                1 -> false
                else -> null
            }
        }, onSubscriptionChange)
    }

    protected abstract fun createItem(itemData: I): Item
    protected abstract fun createFilterView(filter: F): View
    protected abstract fun clearFilterView(filterView: View)

    override fun layout() = R.layout.fragment_search
}