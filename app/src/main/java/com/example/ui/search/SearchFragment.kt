package com.example.ui.search

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.EditText
import com.example.R
import com.example.data.models.InterestNew
import com.example.data.models.SearchFilter
import com.example.databinding.LayoutListSearchBinding
import com.example.extensions.defaultDateFormatter
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.formatToDefaultServerDate
import com.example.holders.NoDataItem
import com.example.interfaces.SearchInterfaceProvider
import com.example.ui.base.BaseFragment
import com.example.ui.base.BaseFragmentNew
import com.example.util.DATE_STRING_FORMAT_SHORT_MONTH_FULL_YEAR
import com.example.util.pagination.PaginationListGroupAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout
import com.xwray.groupie.Group
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import initAsDatePicker
import initDropDownView
import kotlinx.android.synthetic.main.layout_list_search.*
import onTextChanged

abstract class SearchFragment<P : SearchContract.Presenter<I>, I, F : SearchFilter> :
    BaseFragment(), SearchContract.View<I, F> {

    abstract var presenter: P

    private var filterDialog: BottomSheetDialog? = null
    private var filterView: View? = null

    val headerSection = Section()

    var mCanShowEventAndOrganizations = false

    protected val filterNotChosenVariant by lazy { getString(R.string.search_filters_not_chosen) }

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
        (this as? SearchInterfaceProvider)?.apply {
            presenter.onResume(provideSearchInterface())
            return
        }

        (parentFragment as? SearchInterfaceProvider)?.apply {
            presenter.onResume(provideSearchInterface())
            return
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchList.apply {
            adapter = this@SearchFragment.adapter
        }
        swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }

    }

    override fun setData(data: List<I?>) {
        if (data.isEmpty()) {
            adapter.update(
                listOf(
                    NoDataItem(
                        getString(R.string.search_no_data_text),
                        getString(R.string.search_no_data_description)
                    )
                )
            )
        } else {
            Log.e("SearchEventsList", "start")
            Log.e("SearchEventsList", "size: " + data.size)
            adapter.update(data.map(::createItem))
            Log.e("SearchEventsList", "finish")
        }
        swipeToRefresh.isRefreshing = false
    }

    override fun showFilter(filter: F) {
        val filterContainer =
            (layoutInflater.inflate(R.layout.layout_filter, null) as ViewGroup).apply {
                findViewById<ViewGroup>(R.id.flFilters).apply {
                    val filterView = createFilterView(filter)
                    this@SearchFragment.filterView = filterView
                    addView(filterView)
                }

                findViewById<View>(R.id.btnApply).setOnClickListener { presenter.onFilterApplyClick() }
                findViewById<View>(R.id.btnClear).setOnClickListener { presenter.onFilterClearClick() }
                findViewById<View>(R.id.btnClose).setOnClickListener { filterDialog?.dismiss() }
            }

        filterDialog = BottomSheetDialog(requireContext())
            .apply {
                setContentView(filterContainer)
                val behavior = BottomSheetBehavior.from(filterContainer.parent as View)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
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

    protected fun initTextFilter(
        editText: EditText,
        text: String?,
        onTextChange: (String?) -> Unit
    ) {
        editText.apply {
            onTextChanged { onTextChange(it?.toString()) }
            setText(text)
        }
    }

    protected fun initDateFilter(
        editText: EditText,
        inputLayout: TextInputLayout,
        date: String?,
        onDateChange: (String?) -> Unit
    ) {
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

    protected fun initBiFilter(
        textView: AutoCompleteTextView,
        filter: List<String>,
        boolean: Boolean?,
        onSubscriptionChange: (Boolean?) -> Unit
    ) {
        val selectedValue = when (boolean) {
            true -> filter[0]
            false -> filter[1]
            else -> null
        }

        initDropDownView(
            textView, filter.toList(), selectedValue,
            filterNotChosenVariant, findValue = {
                when (filter.indexOf(it)) {
                    0 -> true
                    1 -> false
                    else -> null
                }
            }, onVariantChange = onSubscriptionChange
        )
    }

    protected fun initInterests(
        interests: Map<InterestNew, List<InterestNew>>,
        tvTheme: AutoCompleteTextView,
        tilSpec: TextInputLayout,
        tvSpec: AutoCompleteTextView,
        theme: Int?,
        spec: Int?,
        onInterestChange: (theme: Int?, spec: Int?) -> Unit
    ) {
        var currentTheme = theme
        var currentSpec: Int?

        val onSpecChange: (Int?) -> Unit = {
            currentSpec = it
            onInterestChange(currentTheme, currentSpec)
        }

        val themes = interests.keys
        val selectedTheme = findInterest(theme, themes)
        initDropDownView(
            tvTheme,
            themes,
            selectedTheme?.name,
            null,
            transformKey = { it.name ?: "" },
            findValue = { it?.id },
            onVariantChange = { id ->
                currentTheme = id
                currentSpec = null
                onInterestChange(id, null)
                val specs = findInterest(id, themes)?.let { interests[it] }
                initSpec(tilSpec, tvSpec, specs, null, onSpecChange)
            }
        )

        val specs = selectedTheme?.let { interests[it] }
        initSpec(tilSpec, tvSpec, specs, spec, onSpecChange)
    }

    private fun initSpec(
        inputLayout: View,
        textView: AutoCompleteTextView,
        interests: List<InterestNew>?,
        spec: Int?,
        onSpecChange: (spec: Int?) -> Unit
    ) {
        if (interests == null) {
            textView.isEnabled = false
            textView.text = null
            inputLayout.isEnabled = false
        } else {
            val selectedTheme = findInterest(spec, interests)
            initDropDownView(
                textView,
                interests,
                selectedTheme?.name,
                null,
                transformKey = { it.name ?: "" },
                findValue = { it?.id },
                onVariantChange = { onSpecChange(it) })
            textView.isEnabled = true
            inputLayout.isEnabled = true
        }
    }

    private fun findInterest(id: Int?, interests: Collection<InterestNew>): InterestNew? {
        return id?.let { interests.find { it.id == id } }
    }

    protected abstract fun createItem(itemData: I?): Group
    protected abstract fun createFilterView(filter: F): View
    protected abstract fun clearFilterView(filterView: View)

    override fun layout() = R.layout.layout_list_search
}