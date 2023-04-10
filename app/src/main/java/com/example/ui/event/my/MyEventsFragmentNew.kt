package com.example.ui.event.my

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.EventNew
import com.example.data.models.InterestNew
import com.example.data.models.MyEventsFilter
import com.example.data.models.SearchFilter
import com.example.databinding.FragmentMyEventsBinding
import com.example.extensions.*
import com.example.holders.PlaceholderItem
import com.example.holders.redesign.EventGroupNew
import com.example.holders.redesign.EventItemNew
import com.example.ui.base.BaseFragmentNew
import com.example.ui.event.about.AboutEventFragmentNewArgs
import com.example.ui.event.my.schedule.items.NoScheduleEventItem
import com.example.ui.event.registration.EventRegistrationFragmentArgs
import com.example.ui.views.StateType
import com.example.util.DATE_STRING_FORMAT_SHORT_MONTH_FULL_YEAR
import com.example.util.SearchInput
import com.example.util.pagination.PaginationListGroupAdapter
import com.example.util.smoothScrollToFirstItem
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import initAsDatePicker
import initDropDownView
import kotlinx.android.synthetic.main.layout_filter_event.view.*
import onTextChanged
import javax.inject.Inject
import javax.inject.Provider
import kotlin.math.abs

class MyEventsFragmentNew : BaseFragmentNew<FragmentMyEventsBinding>(), MyEventsContractNew.View {

    @InjectPresenter
    lateinit var presenter: MyEventsPresenterNew

    private var mFilterDialog: BottomSheetDialog? = null
    private var mFilterView: View? = null

    @Inject
    lateinit var presenterProvider: Provider<MyEventsPresenterNew>

    @ProvidePresenter
    fun providePresenter(): MyEventsPresenterNew = presenterProvider.get().apply {
        mEventStateFilter = MyEventsFilter.NONE
    }

    private val eventsSection = Section()


    private val groupAdapter by lazy {
        PaginationListGroupAdapter<GroupieViewHolder>().apply {
            add(eventsSection)
            setOnItemTakeCallback(object : PaginationListGroupAdapter.OnItemTakeCallback {
                override fun onItemTake(position: Int) {
                    presenter.onItemTake(position)
                }
            })
        }
    }


    private val onEventClickListener = object : EventItemNew.OnEventClickListener {
        override fun onActionRegister(event: String) {
            presenter.onActionRegister(event)
        }

        override fun onActionCancel(event: String, registrationId: String?) {
            presenter.onActionCancel(event, registrationId)
        }

        override fun onShowEventClick(view: View, event: String) {
            presenter.onShowEventClick(event)
        }

        override fun onShowUpdateState() = showStateErrorMessage(StateType.BASE, false, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            eventsList.adapter = groupAdapter
            btnDeclined.setOnCheckedChangeListener { _, isChecked ->
                presenter.setEventStateFilter(isChecked, MyEventsFilter.DECLINED)
            }
            btnApproved.setOnCheckedChangeListener { _, isChecked ->
                presenter.setEventStateFilter(isChecked, MyEventsFilter.APPROVED)
            }
            btnPending.setOnCheckedChangeListener { _, isChecked ->
                presenter.setEventStateFilter(isChecked, MyEventsFilter.PENDING)
            }
            mBinding.apply {
                etSearch.apply {
                    SearchInput(this).apply {
                        setOnTextChange { presenter.onSearchTextChange(it) }
                        setOnTextChangeDone {
                            presenter.onSearchTextSubmit(it)
                            hideKeyboard()
                        }
                    }
                    onTextChanged { btnClear.isVisible = !it.isNullOrEmpty() }
                    btnClear.apply {
                        btnClear.isVisible = !etSearch.text.isNullOrEmpty()
                        setOnClickListener { etSearch.text = null }
                    }
                    btnFilter.setOnClickListener { presenter.onShowFiltersClick() }
                    onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                        clSearch.setBackgroundResource(
                            if (hasFocus) R.drawable.background_search_field_rounded_focused
                            else R.drawable.background_search_field_rounded_normal
                        )
                    }
                }
            }
            swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
        }
        initCollapseLabel()
    }

    override fun setData(data: List<EventNew?>) {
        eventsSection.update(data.map {
            if (it == null) PlaceholderItem(PlaceholderItem.Type.EVENT)
            else EventGroupNew(it, onEventClickListener)
        })
        mBinding.swipeToRefresh.isRefreshing = false
    }

    override fun showFilters() {
        val filterContainer =
            (layoutInflater.inflate(R.layout.layout_filter, null) as ViewGroup).apply {
                findViewById<ViewGroup>(R.id.flFilters).apply {
                    val filterView = createEventFiltersView(presenter.getSearchFilters())
                    mFilterView = filterView
                    addView(filterView)
                }

                findViewById<View>(R.id.btnApply).setOnClickListener {
                    presenter.updateData()
                    hideFilter()
                }
                findViewById<View>(R.id.btnClear).setOnClickListener {
                    clearFiltersView()
                }
                findViewById<View>(R.id.btnClose).setOnClickListener {
                    mFilterDialog?.dismiss()
                }
            }

        mFilterDialog = BottomSheetDialog(requireContext())
            .apply {
                setContentView(filterContainer)
                val behavior = BottomSheetBehavior.from(filterContainer.parent as View)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                setOnDismissListener { }
                show()
            }
    }

    override fun setFiltersChosen(isChosen: Boolean) {
        mBinding.btnFilter.apply {
            if (isChosen) setImageResource(R.drawable.ic_filters_selected)
            else setImageResource(R.drawable.ic_filters_new)
        }
    }

    override fun showEmptyListPlaceholder(isFirst: Boolean) {
        if (isFirst) {
            eventsSection.update(
                listOf(
                    NoScheduleEventItem(
                        getString(R.string.no_event_schedule_you_have),
                        getString(R.string.choose_event_and_do_request),
                        60.dp
                    )
                )
            )
        } else {
            eventsSection.update(
                listOf(
                    NoScheduleEventItem(
                        getString(R.string.no_data_found),
                        getString(R.string.no_event_with_params_title),
                        60.dp
                    )
                )
            )
        }

        mBinding.swipeToRefresh.isRefreshing = false
    }

    fun smoothScrollToFirstItem() {
        val mLayoutManager = mBinding.eventsList.layoutManager as LinearLayoutManager
        mLayoutManager.smoothScrollToFirstItem(requireContext(), mBinding.appBarLayout, 1)
    }

    override fun showAboutEvent(event: String) {
        findNavController().navigate(
            R.id.about_event_fragment_new,
            AboutEventFragmentNewArgs.Builder(event).build().toBundle()
        )
    }

    override fun showEventRequest(event: String) {
        findNavController().navigate(
            R.id.request_fragment,
            EventRegistrationFragmentArgs.Builder(event).build().toBundle()
        )
    }

    private fun showMyScheduleEvents() {
        findNavController().navigate(R.id.my_schedule_events_fragment)
    }

    override fun setActionButton(event: EventNew?) {
        eventsSection.findGroupBy<EventGroupNew> { true }?.updateButtonState(event)
    }

    override fun setShowMyScheduleButton(canShow: Boolean) {
        mBinding.toolbar.apply {
            isVisible = canShow
            mBinding.btnGoToMyTimeTable.setOnClickListener {
                showMyScheduleEvents()
            }
        }
    }


    private fun createEventFiltersView(filter: SearchFilter.EventNew): View {
        return layoutInflater.inflate(R.layout.layout_filter_event, null).apply {
            etAddress.apply {
                setTextWithoutSearch(filter.address)
                onTextChanged {
                    filter.address = it.toString()
                    filter.fullAddress = null
                }
                onDataSelectedListener = {
                    filter.fullAddress = it
                }
            }

            initTextFilter(etName, filter.name) { filter.name = it }
            initDateFilter(etStart, tilStart, filter.dateStart) { filter.dateStart = it }
            initDateFilter(etFinish, tilFinish, filter.dateFinish) { filter.dateFinish = it }

            val interests = filter.interests
            if (interests.isNullOrEmpty()) {
                tilTheme.isVisible = false
                tilSpec.isVisible = false
            } else {
                initInterests(
                    interests,
                    tvTheme,
                    tilSpec,
                    tvSpec,
                    filter.theme,
                    filter.spec
                ) { theme, spec ->
                    filter.theme = theme
                    filter.spec = spec
                }
                tilTheme.isVisible = true
                tilSpec.isVisible = true
            }

            val formats = filter.formats
            if (formats.isNullOrEmpty()) {
                tilFormat.isVisible = false
            } else {
                tilFormat.isVisible = true
                initDropDownView(
                    tvFormat,
                    formats,
                    formats.find { it.id == filter.format }?.name,
                    null,
                    { it.name ?: "" },
                    { it?.id },
                    { filter.format = it }
                )
            }

        }
    }

    private fun initTextFilter(editText: EditText, text: String?, onTextChange: (String?) -> Unit) {
        editText.apply {
            onTextChanged { onTextChange(it?.toString()) }
            setText(text)
        }
    }

    private fun initDateFilter(
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

    private fun initInterests(
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

    private fun hideFilter() {
        mFilterDialog?.apply {
            setOnDismissListener(null)
            dismiss()
        }
    }

    private fun clearFiltersView() {
        mFilterView?.apply {
            etAddress?.text?.clear()
            etName.text?.clear()
            etStart.text?.clear()
            etFinish.text?.clear()
            tvTheme.text.clear()
            tvSpec.text.clear()
            tvFormat.text.clear()
        }
    }

    private fun initCollapseLabel() {
        mBinding.appBarLayout.addOnOffsetChangedListener(
            AppBarLayout.OnOffsetChangedListener { appBarLayout, i ->
                updateViews(abs(i / appBarLayout.totalScrollRange.toFloat()))
            })
    }


    private fun updateViews(offset: Float) {

        when {
            offset < SWITCH_BOUND -> Pair(TO_EXPANDED, cashCollapseState?.second ?: WAIT_FOR_SWITCH)
            else -> Pair(TO_COLLAPSED, cashCollapseState?.second ?: WAIT_FOR_SWITCH)
        }.apply {
            when {
                cashCollapseState != null && cashCollapseState != this -> {
                    when (first) {
                        TO_EXPANDED -> {
                            mBinding.apply {
                                tvLabelLarge.apply {
                                    visibility = View.VISIBLE
                                    alpha = 0F
                                    animate().setDuration(500).alpha(1.0f)
                                }
                            }
                        }
                        TO_COLLAPSED -> {
                            mBinding.apply {
                                tvLabelLarge.apply {
                                    alpha = 1F
                                    animate().setDuration(500).alpha(0.0f)
                                    visibility = View.GONE
                                }
                            }

                        }
                    }
                    cashCollapseState = Pair(first, SWITCHED)
                }
                else -> {
                    cashCollapseState = Pair(first, WAIT_FOR_SWITCH)
                }
            }
        }
    }

    companion object {
        const val SWITCH_BOUND = 0.3f
        const val TO_EXPANDED = 0
        const val TO_COLLAPSED = 1
        const val WAIT_FOR_SWITCH = 0
        const val SWITCHED = 1
        private var cashCollapseState: Pair<Int, Int>? = null
    }


    override fun layout(): Int = R.layout.fragment_my_events
}