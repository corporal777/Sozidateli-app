package com.example.ui.views.filters.organization

import android.content.Context
import android.view.LayoutInflater
import com.example.App
import com.example.app.databinding.BottomSheetOrganizationFiltersBinding
import com.example.data.models.InterestNew
import com.example.data.models.NewEventFormat
import com.example.data.models.SearchFilter
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

class OrgFiltersBottomSheetDialog(
    val requireContext: Context,
    filter: SearchFilter.Organization
) : BottomSheetDialog(requireContext), OrgFiltersBottomSheetContract.View {

    private val mBinding =
        BottomSheetOrganizationFiltersBinding.inflate(LayoutInflater.from(requireContext))
    private val mvpDelegate by lazy { MvpDelegate(this) }

    private var filterInn = filter.inn
    private var filterName = filter.name

    private var filterRegion = filter.addressRegion
    private var filterTown = filter.addressTown
    private var filterTownType = filter.addressTownType


    @InjectPresenter
    lateinit var presenter: OrgFiltersBottomSheetPresenter

    @Inject
    lateinit var presenterProvider: Provider<OrgFiltersBottomSheetPresenter>

    @ProvidePresenter
    fun providePresenter(): OrgFiltersBottomSheetPresenter = presenterProvider.get()

    private var onFiltersApply: (filter: SearchFilter.Organization) -> Unit = {}

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
                onFiltersApply.invoke(getOrgFilters())
                dismiss()
            }
        }
    }

    override fun initOrganizationName() {
        mBinding.etOrganizationName.initInput(filterName) {
            filterName = it?.toString()
        }
    }

    override fun initOrganizationInn() {
        mBinding.etInn.initInput(filterInn) {
            filterInn = it?.toString()
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


    override fun initFormats(formats: List<NewEventFormat>) {}
    override fun initInterests(interests: Map<InterestNew, List<InterestNew>>) {}


    private fun clearFiltersView() {
        mBinding.apply {
            tvRegion.text = null
            tvTown.text = null
            etOrganizationName.text = null
            etInn.text = null
        }
    }

    fun setFiltersSelected(block: (filters: SearchFilter.Organization) -> Unit): OrgFiltersBottomSheetDialog {
        onFiltersApply = block
        return this
    }

    private fun getOrgFilters(): SearchFilter.Organization {
        return SearchFilter.Organization(
            addressRegion = filterRegion,
            addressTown = filterTown,
            addressTownType = filterTownType,

            name = filterName,
            inn = filterInn
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