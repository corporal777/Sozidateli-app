package com.example.ui.views.filters.user

import android.content.Context
import android.view.LayoutInflater
import com.example.App
import com.example.app.databinding.BottomSheetUserFiltersBinding
import com.example.data.models.InterestNew
import com.example.data.models.NewEventFormat
import com.example.data.models.SearchFilter
import com.example.extensions.initDropDownView
import com.example.ui.views.suggestFieldView.region.SearchRegionBottomSheet
import com.example.ui.views.suggestFieldView.town.SearchTownBottomSheet
import com.example.util.initInput
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import moxy.MvpDelegate
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class UserFiltersBottomSheetDialog(
    val requireContext: Context,
    filter: SearchFilter.UserNew
) : BottomSheetDialog(requireContext), UserFiltersBottomSheetContract.View {

    private val mBinding = BottomSheetUserFiltersBinding.inflate(LayoutInflater.from(requireContext))
    private val mvpDelegate by lazy { MvpDelegate(this) }

    private var filterAgeFrom = filter.ageFrom
    private var filterAgeTo = filter.ageTo
    private var filterTheme = filter.theme
    private var filterSpec = filter.spec
    private var filterRegion = filter.addressRegion
    private var filterTown = filter.addressTown
    private var filterTownType = filter.addressTownType


    @InjectPresenter
    lateinit var presenter: UserFiltersBottomSheetPresenter

    @Inject
    lateinit var presenterProvider: Provider<UserFiltersBottomSheetPresenter>

    @ProvidePresenter
    fun providePresenter(): UserFiltersBottomSheetPresenter = presenterProvider.get()

    private var onFiltersApply: (filter: SearchFilter.UserNew) -> Unit = {}

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
                onFiltersApply.invoke(getUserFilters())
                dismiss()
            }
        }
    }


    override fun initRegions() {
        mBinding.apply {
            tilRegion.setEndIconOnClickListener { tvRegion.performClick() }
            tvRegion.apply {
                isCursorVisible = false
                isFocusable = false
                isFocusableInTouchMode = false

                setOnClickListener {
                    SearchRegionBottomSheet(requireContext)
                        .setRegionSelectedCallback {
                            filterRegion = it?.name
                            this.setText(it?.name)
                        }.show()
                }
                initInput(filterRegion) {
                    tilTown.isEnabled = !it.isNullOrBlank()
                    if (it.isNullOrBlank()) filterRegion = null
                }
            }
        }

    }

    override fun initTowns() {
        mBinding.apply {
            tilTown.apply {
                isEnabled = !filterRegion.isNullOrBlank()
                setEndIconOnClickListener {
                    tvTown.performClick()
                }
            }
            tvTown.apply {
                isCursorVisible = false
                isFocusable = false
                isFocusableInTouchMode = false

                setOnClickListener {
                    SearchTownBottomSheet(requireContext, filterRegion, "user")
                        .setTownSelectedCallback {
                            filterTown = it?.name
                            filterTownType = it?.type
                            this.setText(it?.name)
                        }.show()
                }
                initInput(filterTown) {
                    if (it.isNullOrBlank()) {
                        filterTown = null
                        filterTownType = null
                    }
                }
            }
        }

    }

    override fun initAgeFrom() {
        mBinding.apply {
            initDropDownView(
                tvAgeFrom,
                presenter.getAgesList(null),
                presenter.getAgesList(null).find { it.toInt() == filterAgeFrom },
                null,
                { it },
                { it },
                {
                    filterAgeFrom = it?.toInt()
                    initAgeTo()
                }
            )
        }
    }

    override fun initAgeTo() {
        mBinding.apply {
            initDropDownView(
                tvAgeTo,
                presenter.getAgesList(filterAgeFrom),
                presenter.getAgesList(filterAgeFrom).find { it.toInt() == filterAgeTo },
                null,
                { it },
                { it },
                { filterAgeTo = it?.toInt() }
            )
        }
    }

    override fun initFormats(formats: List<NewEventFormat>) {}

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
            tvRegion.text = null
            tvTown.text = null
            tvTheme.text = null
            tvSpec.text = null
            tvAgeFrom.text = null
            tvAgeTo.text = null
        }
    }

    fun setFiltersSelected(block: (filters: SearchFilter.UserNew) -> Unit): UserFiltersBottomSheetDialog {
        onFiltersApply = block
        return this
    }

    private fun getUserFilters(): SearchFilter.UserNew {
        return SearchFilter.UserNew(
            addressRegion = filterRegion,
            addressTown = filterTown,
            addressTownType = filterTownType,
            theme = filterTheme,
            spec = filterSpec,
            ageFrom = filterAgeFrom,
            ageTo = filterAgeTo
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