package com.example.ui.views.filters.event

import android.content.Context
import android.view.LayoutInflater
import androidx.core.view.isVisible
import com.example.App
import com.example.data.models.InterestNew
import com.example.data.models.NewEventFormat
import com.example.data.models.SearchFilter
import com.example.databinding.BottomSheetEventFiltersBinding
import com.example.extensions.defaultDateFormatter
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.formatToDefaultServerDate
import com.example.util.DATE_STRING_FORMAT_SHORT_MONTH_FULL_YEAR
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.example.extensions.initAsDatePicker
import com.example.extensions.initDropDownView
import moxy.MvpDelegate
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import com.example.extensions.onTextChanged
import javax.inject.Inject
import javax.inject.Provider

class EventFiltersBottomSheetDialog(
    context: Context,
    filter: SearchFilter.EventNew
) : BottomSheetDialog(context), EventFiltersBottomSheetContract.View {

    private val mBinding = BottomSheetEventFiltersBinding.inflate(LayoutInflater.from(context))
    private val mvpDelegate by lazy { MvpDelegate(this) }

    private var filterName = filter.name
    private var filterDateStart = filter.dateStart
    private var filterDateFinish = filter.dateFinish
    private var filterFormat = filter.format
    private var filterTheme = filter.theme
    private var filterSpec = filter.spec
    private var filterAddress = filter.address
    private var filterFullAddress = filter.fullAddress


    @InjectPresenter
    lateinit var presenter: EventFiltersBottomSheetPresenter

    @Inject
    lateinit var presenterProvider: Provider<EventFiltersBottomSheetPresenter>

    @ProvidePresenter
    fun providePresenter(): EventFiltersBottomSheetPresenter = presenterProvider.get()

    private var onFiltersApply: (filter: SearchFilter.EventNew) -> Unit = {}

    init {
        setContentView(mBinding.root)
        (context.applicationContext as App).appComponent.inject(this)
        this.behavior.apply {
            skipCollapsed = true
            state = BottomSheetBehavior.STATE_EXPANDED
        }

        mBinding.apply {
            btnClose.setOnClickListener { dismiss() }
            btnClear.setOnClickListener { clearFiltersView() }
            btnApply.setOnClickListener {
                onFiltersApply.invoke(getEventFilters())
                dismiss()
            }
        }
    }

    override fun initTextFilter() {
        mBinding.etName.apply {
            onTextChanged { filterName = it.toString() }
            setText(filterName)
        }
    }

    override fun initAddressFilter() {
        mBinding.etAddress.apply {
            setTextWithoutSearch(filterAddress)
            onTextChanged {
                filterAddress = it.toString()
                filterFullAddress = null
            }
            onDataSelectedListener = {
                filterFullAddress = it
            }
        }
    }

    override fun initDateFilter() {
        mBinding.etStart.apply {
            val parsedDate = filterDateStart?.let { defaultServerDateFormatter.parse(it) }
            val formattedDate = parsedDate?.let { defaultDateFormatter.format(it) }
            onTextChanged {
                filterDateStart = it?.toString()?.formatToDefaultServerDate()
            }
            setText(formattedDate)
            mBinding.tilStart.initAsDatePicker(parsedDate) { year, month, day ->
                String.format(DATE_STRING_FORMAT_SHORT_MONTH_FULL_YEAR, day, month + 1, year)
            }
        }


        mBinding.etFinish.apply {
            val parsedDate = filterDateFinish?.let { defaultServerDateFormatter.parse(it) }
            val formattedDate = parsedDate?.let { defaultDateFormatter.format(it) }
            onTextChanged {
                filterDateFinish = it?.toString()?.formatToDefaultServerDate()
            }
            setText(formattedDate)
            mBinding.tilFinish.initAsDatePicker(parsedDate) { year, month, day ->
                String.format(DATE_STRING_FORMAT_SHORT_MONTH_FULL_YEAR, day, month + 1, year)
            }
        }
    }

    override fun initFormats(formats: List<NewEventFormat>) {
        if (formats.isNullOrEmpty()) {
            mBinding.tilFormat.isVisible = false
        } else {
            mBinding.tilFormat.isVisible = true
            initDropDownView(
                mBinding.tvFormat,
                formats,
                formats.find { it.id == filterFormat }?.name,
                null,
                { it.name ?: "" },
                { it?.id },
                { filterFormat = it }
            )
        }
    }

    override fun initInterests(interests: Map<InterestNew, List<InterestNew>>) {
        val themes = interests.keys
        val selectedTheme = presenter.findInterest(filterTheme, themes)
        initDropDownView(
            mBinding.tvTheme,
            themes,
            selectedTheme?.name,
            null,
            transformKey = { it.name ?: "" },
            findValue = { it?.id },
            onVariantChange = { id ->
                filterTheme = id
                filterSpec = null

                val specs = presenter.findInterest(id, themes)?.let { interests[it] }
                initSpecializations(specs, filterSpec)
            }
        )

        val specs = selectedTheme?.let { interests[it] }
        initSpecializations(specs, filterSpec)
    }

    private fun initSpecializations(interests: List<InterestNew>?, spec: Int?) {
        if (interests == null) {
            mBinding.tvSpec.apply {
                isEnabled = false
                text = null
            }
            mBinding.tilSpec.isEnabled = false
        } else {
            mBinding.tvSpec.isEnabled = true
            mBinding.tilSpec.isEnabled = true
            val selectedTheme = presenter.findInterest(spec, interests)
            initDropDownView(
                mBinding.tvSpec,
                interests,
                selectedTheme?.name,
                null,
                transformKey = { it.name ?: "" },
                findValue = { it?.id },
                onVariantChange = {
                    filterSpec = it
                })
        }
    }

    private fun clearFiltersView() {
        mBinding.apply {
            etAddress.text?.clear()
            etName.text?.clear()
            etStart.text?.clear()
            etFinish.text?.clear()
            tvTheme.text.clear()
            tvSpec.text.clear()
            tvFormat.text.clear()
        }
    }

    fun setFiltersSelected(block: (filters: SearchFilter.EventNew) -> Unit): EventFiltersBottomSheetDialog {
        onFiltersApply = block
        return this
    }

    private fun getEventFilters(): SearchFilter.EventNew {
        return SearchFilter.EventNew(
            name = filterName,
            dateStart = filterDateStart,
            dateFinish = filterDateFinish,
            format = filterFormat,
            theme = filterTheme,
            spec = filterSpec,
            address = filterAddress,
            fullAddress = filterFullAddress
        )
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mvpDelegate.onCreate()
        mvpDelegate.onAttach()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mvpDelegate.onSaveInstanceState()
        mvpDelegate.onDetach()
        mvpDelegate.onDestroyView()
        mvpDelegate.onDestroy()
    }
}