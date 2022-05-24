package com.example.ui.event.my

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.*
import com.example.extensions.*
import com.example.holders.EventStatusItem
import com.example.holders.NoDataItem
import com.example.holders.PlaceholderItem
import com.example.holders.redesign.EventGroupNew
import com.example.holders.redesign.EventItemNew
import com.example.holders.redesign.ScreenHeaderItem
import com.example.ui.base.BaseFragment
import com.example.ui.event.about.redesign.AboutEventFragmentNewArgs
import com.example.ui.event.my.items.NoEventItem
import com.example.ui.event.my.items.SearchEventItem
import com.example.ui.event.my.items.TagsItem
import com.example.ui.event.registration.EventRegistrationFragmentArgs
import com.example.ui.views.StateType
import com.example.util.DATE_STRING_FORMAT_SHORT_MONTH_FULL_YEAR
import com.example.util.pagination.PaginationListGroupAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import initAsDatePicker
import initDropDownView
import kotlinx.android.synthetic.main.fragment_my_events.*
import kotlinx.android.synthetic.main.fragment_my_events.swipeToRefresh
import kotlinx.android.synthetic.main.layout_filter_event.view.*
import kotlinx.android.synthetic.main.layout_list.*
import onTextChanged
import javax.inject.Inject
import javax.inject.Provider

class MyEventsFragmentNew : BaseFragment(), MyEventsContractNew.View {

    @InjectPresenter
    lateinit var presenter: MyEventsPresenterNew

    private var mDy = 0
    private var mFilterDialog: BottomSheetDialog? = null
    private var mFilterView: View? = null

    @Inject
    lateinit var presenterProvider: Provider<MyEventsPresenterNew>

    @ProvidePresenter
    fun providePresenter(): MyEventsPresenterNew = presenterProvider.get().apply {
        mEventStateFilter = MyEventsFilter.NONE
    }

    private val headerSection by lazy {
        Section().apply {
            add(ScreenHeaderItem(getString(R.string.my_events_title)))
        }
    }
    private val searchSection = Section()
    private val tagsSection = Section()
    private val eventsSection = Section()


    private val groupAdapter by lazy {
        PaginationListGroupAdapter<GroupieViewHolder>().apply {
            add(headerSection)
            add(searchSection)
            add(tagsSection)
            add(eventsSection)
            setOnItemTakeCallback(object : PaginationListGroupAdapter.OnItemTakeCallback {
                override fun onItemTake(position: Int) {
                    presenter.onItemTake(position)
                }
            })
        }
    }


    private val onEventClickListener = object : EventItemNew.OnEventClickListener {
        override fun onActionRegister(event: String) {presenter.onActionRegister(event)}
        override fun onActionCancel(event: String, registrationId: String?) {presenter.onActionCancel(event, registrationId)}
        override fun onShowEventClick(view: View, event: String) {presenter.onShowEventClick(event)}
        override fun onShowUpdateState() = showStateErrorMessage(StateType.BASE, false, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        eventsList.apply {
            adapter = groupAdapter

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    mDy += dy
                    if (mDy >= 30) {
                        appBarLayout.elevation = 10f
                    } else {
                        appBarLayout.elevation = 0f
                    }
                }
            })
        }

        btnGoToMyTimeTable.setOnClickListener {

        }

        swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
    }

    override fun setData(data: List<EventNew?>) {
        eventsSection.update(data.map {
            if (it == null) PlaceholderItem(PlaceholderItem.Type.EVENT)
            else EventGroupNew(
                it,
                onEventClickListener,
            )
        })
        swipeToRefresh.isRefreshing = false
    }


    override fun setTagsBlock(listTags: List<Tag>) {
        tagsSection.update(listOf(TagsItem(listTags) {
            presenter.setEventStateFilter(it)
        }))
    }

    override fun setSearchBlock() {
        searchSection.update(listOf(SearchEventItem({
            presenter.onSearchTextChange(it)
        }, {
            presenter.onSearchTextSubmit(it)
            hideKeyboard()
        }, {
            presenter.onShowFiltersClick()
        })))
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

    override fun showEmptyListPlaceholder() {
        eventsSection.update(listOf(NoEventItem(getString(R.string.empty_list_placeholder_message),getString(R.string.no_event_with_params_title))))
        swipeToRefresh.isRefreshing = false
    }

    override fun showAboutEvent(event: String) {
        findNavController().navigate(
            R.id.about_event_fragment_new,
            AboutEventFragmentNewArgs.Builder(event).build().toBundle())
    }

    override fun showEventRequest(event: String) {
        findNavController().navigate(
            R.id.request_fragment,
            EventRegistrationFragmentArgs.Builder(event).build().toBundle()
        )
    }

    override fun setActionButton(event: EventNew?) {
        eventsSection.findGroupBy<EventGroupNew> { true }?.updateButtonState(event)
    }


    private fun createEventFiltersView(filter : SearchFilter.EventNew): View {
        return layoutInflater.inflate(R.layout.layout_filter_event, null).apply {
            etAddress.apply {
                setTextWithoutSearch(filter.address)
                onTextChanged {
                    filter.address = it.toString()
                    filter.fullAddress = null
                }
                onDataSelectedListener = {
                    filter.fullAddress = it
                    x
                }
            }
            initTextFilter(etName, filter.name){filter.name = it}
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

    private fun initDateFilter(editText: EditText, inputLayout: TextInputLayout, date: String?, onDateChange: (String?) -> Unit) {
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

    private fun initInterests(interests: Map<InterestNew, List<InterestNew>>, tvTheme: AutoCompleteTextView, tilSpec: TextInputLayout, tvSpec: AutoCompleteTextView, theme: Int?, spec: Int?, onInterestChange: (theme: Int?, spec: Int?) -> Unit) {
        var currentTheme = theme
        var currentSpec: Int?

        val onSpecChange: (Int?) -> Unit = {
            currentSpec = it
            onInterestChange(currentTheme, currentSpec)
        }

        val themes = interests.keys
        val selectedTheme = findInterest(theme, themes)
        initDropDownView(tvTheme, themes, selectedTheme?.name, null, transformKey = { it.name?: "" }, findValue = { it?.id }, onVariantChange = { id ->
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

    private fun initSpec(inputLayout: View, textView: AutoCompleteTextView, interests: List<InterestNew>?, spec: Int?, onSpecChange: (spec: Int?) -> Unit) {
        if (interests == null) {
            textView.isEnabled = false
            textView.text = null
            inputLayout.isEnabled = false
        } else {
            val selectedTheme = findInterest(spec, interests)
            initDropDownView(textView, interests, selectedTheme?.name, null, transformKey = { it.name?: "" }, findValue = { it?.id }, onVariantChange = { onSpecChange(it) })
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

    override fun onStart() {
        super.onStart()
        if (eventsList != null) {
            mDy += eventsList.scrollY
        }
    }

    override fun layout(): Int = R.layout.fragment_my_events
}