package com.example.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.EditText
import com.example.R
import com.example.data.models.InterestNew
import com.example.data.models.SearchFilter
import com.example.data.models.SearchTown
import com.example.databinding.LayoutFilterBinding
import com.example.databinding.LayoutFilterEventSearchBinding
import com.example.databinding.LayoutListSearchBinding
import com.example.extensions.defaultDateFormatter
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.formatToDefaultServerDate
import com.example.interfaces.SearchInterfaceProvider
import com.example.ui.base.BaseFragmentNew
import com.example.ui.event.list.recommendations.items.NoEventItem
import com.example.ui.search.event.SearchEventPresenter
import com.example.ui.search.tabs.SearchTabsFragment
import com.example.ui.views.suggestFieldView.region.SearchRegionBottomSheet
import com.example.ui.views.suggestFieldView.town.SearchTownBottomSheet
import com.example.util.DATE_STRING_FORMAT_SHORT_MONTH_FULL_YEAR
import com.example.util.initInput
import com.example.util.pagination.PaginationListGroupAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout
import com.xwray.groupie.Group
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import initAsDatePicker
import initDropDownView
import onTextChanged

abstract class SearchFragment<P : SearchContract.Presenter<I>, I, F : SearchFilter> :
    BaseFragmentNew<LayoutListSearchBinding>(), SearchContract.View<I, F> {

    abstract var searchPresenter: P

    private var filterDialog: BottomSheetDialog? = null
    private var filterView: View? = null

    val headerSection = Section()


    protected val filterNotChosenVariant by lazy { getString(R.string.search_filters_not_chosen) }

    protected val adapter by lazy {
        PaginationListGroupAdapter<GroupieViewHolder>().apply {
            setOnItemTakeCallback(object : PaginationListGroupAdapter.OnItemTakeCallback {
                override fun onItemTake(position: Int) {
                    searchPresenter.onItemTake(position)
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        (this as? SearchInterfaceProvider)?.apply {
            searchPresenter.onResume(provideSearchInterface())
            return
        }
        (parentFragment as? SearchInterfaceProvider)?.apply {
            searchPresenter.onResume(provideSearchInterface())
            return
        }
    }

    override fun setHasFilter() {
        (parentFragment as? SearchTabsFragment)?.apply {
            setFiltersChosen(searchPresenter.isHasFilter())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.searchList.apply {
            adapter = this@SearchFragment.adapter
        }
        mBinding.swipeToRefresh.setOnRefreshListener { searchPresenter.onRefreshRequest() }

    }

    override fun setData(data: List<I?>) {
        if (data.isEmpty()) {
            adapter.update(
                listOf(
                    NoEventItem(
                        getString(R.string.search_no_data_text),
                        getString(R.string.search_no_data_description)
                    )
                )
            )
        } else {
            adapter.update(data.map(::createItem))
        }
        mBinding.swipeToRefresh.isRefreshing = false
    }

    override fun showFilter(filter: F) {
        val filterContainer =
            LayoutFilterBinding.inflate(LayoutInflater.from(requireContext()), null, false).apply {
                flFilters.apply {
                    val filterView = createFilterView(filter)
                    this@SearchFragment.filterView = filterView
                    addView(filterView)
                }
                btnApply.setOnClickListener {
                    searchPresenter.onFilterApplyClick()
                    setHasFilter()
                }
                btnClear.setOnClickListener { searchPresenter.onFilterClearClick() }
                btnClose.setOnClickListener { filterDialog?.dismiss() }
            }
        filterDialog = BottomSheetDialog(requireContext())
            .apply {
                setContentView(filterContainer.root)
                val behavior = BottomSheetBehavior.from(filterContainer.root.parent as View)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                setOnDismissListener { searchPresenter.onFilterCancel() }
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

    protected fun initRegions(
        filter : F?,
        tvRegion: AutoCompleteTextView,
        tilRegion: TextInputLayout,
        tilTown: TextInputLayout
    ) {
        tilRegion.setEndIconOnClickListener {
            tvRegion.performClick()
        }
        tvRegion.apply {
            isCursorVisible = false
            isFocusable = false
            isFocusableInTouchMode = false

            setOnClickListener {
                SearchRegionBottomSheet(requireContext())
                    .setRegionSelectedCallback {
                        filter?.addressRegion = it?.name
                        this.setText(it?.name)
                    }
                    .show()
            }
            initInput(filter?.addressRegion) {
                tilTown.isEnabled = !it.isNullOrBlank()
                if (it.isNullOrBlank()) filter?.addressRegion = null
            }
        }
    }

    protected fun initTowns(
        filter : F?,
        tvTown: AutoCompleteTextView,
        tilTown: TextInputLayout,
    ) {
        tilTown.apply {
            isEnabled = !filter?.addressRegion.isNullOrBlank()
            setEndIconOnClickListener {
                tvTown.performClick()
            }
        }
        tvTown.apply {
            isCursorVisible = false
            isFocusable = false
            isFocusableInTouchMode = false

            setOnClickListener {
                SearchTownBottomSheet(
                    requireContext(),
                    filter?.addressRegion,
                    searchPresenter.getSearchType()
                )
                    .setTownSelectedCallback {
                        filter?.addressTown = it?.name
                        filter?.addressTownType = it?.type
                        this.setText(it?.name)
                    }
                    .show()
            }
            initInput(filter?.addressTown) {
                if (it.isNullOrBlank()){
                    filter?.addressTown = null
                    filter?.addressTownType = null
                }
            }
        }
    }


    protected abstract fun createItem(itemData: I?): Group
    protected abstract fun createFilterView(filter: F): View
    protected abstract fun clearFilterView(filterView: View)

    override fun layout() = R.layout.layout_list_search
}