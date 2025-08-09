package com.example.ui.views.filters.event

//class EventFiltersBottomSheetDialog (
//    val requireContext: Context,
//    filter: SearchFilter.EventNew
//) : BottomSheetDialog(requireContext), EventFiltersBottomSheetContract.View {
//
//    private val mBinding = BottomSheetEventFiltersBinding.inflate(LayoutInflater.from(requireContext))
//    private val mvpDelegate by lazy { MvpDelegate(this) }
//
//    private var filterName = filter.name
//    private var filterDateStart = filter.dateStart
//    private var filterDateFinish = filter.dateFinish
//
//    private var filterTheme = filter.theme
//    private var filterSpec = filter.spec
//
//    private var filterFormat = filter.format
//    private var filterCustomFormat = filter.customFormat
//
//    private var filterOrganizationId = filter.organizationId
//    private var filterOrganizationName = filter.organizationName
//
//    private var filterRegion = filter.addressRegion
//    private var filterTown = filter.addressTown
//    private var filterTownType = filter.addressTownType
//
//
//    @InjectPresenter
//    lateinit var presenter: EventFiltersBottomSheetPresenter
//
//    @Inject
//    lateinit var presenterProvider: Provider<EventFiltersBottomSheetPresenter>
//
//    @ProvidePresenter
//    fun providePresenter(): EventFiltersBottomSheetPresenter = presenterProvider.get()
//
//    private var onFiltersApply: (filter: SearchFilter.EventNew) -> Unit = {}
//
//    init {
//        setContentView(mBinding.root)
//        (context.applicationContext as App).appComponent.inject(this)
//        this.behavior.apply {
//            skipCollapsed = true
//            state = BottomSheetBehavior.STATE_EXPANDED
//        }
//
//        mBinding.apply {
//            btnClose.setOnClickListener { dismiss() }
//            btnClear.setOnClickListener { clearFiltersView() }
//            btnApply.setOnClickListener {
//                onFiltersApply.invoke(getEventFilters())
//                dismiss()
//            }
//        }
//    }
//
//    override fun initTextFilter() {
//        mBinding.etName.apply {
//            onTextChanged { filterName = it.toString() }
//            setText(filterName)
//        }
//    }
//
//    override fun initRegions() {
//        mBinding.apply {
//            tilRegion.setEndIconOnClickListener { tvRegion.performClick() }
//            tvRegion.apply {
//                isCursorVisible = false
//                isFocusable = false
//                isFocusableInTouchMode = false
//
//                setOnClickListener {
//                    SearchRegionBottomSheet(requireContext)
//                        .setRegionSelectedCallback {
//                            filterRegion = it?.name
//                            this.setText(it?.name)
//                        }.show()
//                }
//                initInput(filterRegion) {
//                    tilTown.isEnabled = !it.isNullOrBlank()
//                    if (it.isNullOrBlank()) filterRegion = null
//                }
//            }
//        }
//
//    }
//
//    override fun initTowns() {
//        mBinding.apply {
//            tilTown.apply {
//                isEnabled = !filterRegion.isNullOrBlank()
//                setEndIconOnClickListener {
//                    tvTown.performClick()
//                }
//            }
//            tvTown.apply {
//                isCursorVisible = false
//                isFocusable = false
//                isFocusableInTouchMode = false
//
//                setOnClickListener {
//                    SearchTownBottomSheet(requireContext, filterRegion, "event")
//                        .setTownSelectedCallback {
//                            filterTown = it?.name
//                            filterTownType = it?.type
//                            this.setText(it?.name)
//                        }.show()
//                }
//                initInput(filterTown) {
//                    if (it.isNullOrBlank()) {
//                        filterTown = null
//                        filterTownType = null
//                    }
//                }
//            }
//        }
//
//    }
//
//
//    override fun initDateStart(){
//        mBinding.etStart.apply {
//            val parsedDate = filterDateStart?.let { defaultServerDateFormatter.parse(it) }
//            val formattedDate = parsedDate?.let { defaultDateFormatter.format(it) }
//            onTextChanged {
//                filterDateStart = it?.toString()?.formatToDefaultServerDate()
//            }
//            setText(formattedDate)
//            mBinding.tilStart.initAsDatePicker(parsedDate) { year, month, day ->
//                String.format(DATE_STRING_FORMAT_SHORT_MONTH_FULL_YEAR, day, month + 1, year)
//            }
//        }
//    }
//
//    override fun initDateEnd() {
//        mBinding.etFinish.apply {
//            val parsedDate = filterDateFinish?.let { defaultServerDateFormatter.parse(it) }
//            val formattedDate = parsedDate?.let { defaultDateFormatter.format(it) }
//            onTextChanged {
//                filterDateFinish = it?.toString()?.formatToDefaultServerDate()
//            }
//            setText(formattedDate)
//            mBinding.tilFinish.initAsDatePicker(parsedDate) { year, month, day ->
//                String.format(DATE_STRING_FORMAT_SHORT_MONTH_FULL_YEAR, day, month + 1, year)
//            }
//        }
//    }
//
//    override fun initFormats(formats: List<NewEventFormat>) {
//        mBinding.tvFormat.apply {
//            mBinding.tilFormat.endIconMode = TextInputLayout.END_ICON_NONE
//            isCursorVisible = false
//            isFocusable = false
//            isFocusableInTouchMode = false
//
//            setOnClickListener {
//                EventFormatBottomSheet(requireContext, formats)
//                    .setFormatSelectedCallback {
//                        filterFormat = it?.id
//                        filterCustomFormat = it?.name
//                        this.setText(presenter.getFormatName(formats, filterFormat, filterCustomFormat))
//                    }
//                    .show()
//            }
//            initInput(presenter.getFormatName(formats, filterFormat, filterCustomFormat)) {
//                if (it.isNullOrBlank()) {
//                    filterFormat = null
//                    filterCustomFormat = null
//                }
//            }
//        }
//    }
//
//    override fun initOrganizations(organizations: List<OrganizationNew>?) {
//        mBinding.tvOrganization.apply {
//            mBinding.tilOrganization.endIconMode = TextInputLayout.END_ICON_NONE
//            isCursorVisible = false
//            isFocusable = false
//            isFocusableInTouchMode = false
//
//            setOnClickListener {
//                EventOrgBottomSheet(requireContext, organizations)
//                    .setOrganizationSelectedCallback {
//                        filterOrganizationId = it?.id
//                        filterOrganizationName = it?.legalInformation?.name?.short
//
//                        this.setText(presenter.getOrgName(organizations, filterOrganizationName, filterOrganizationId))
//                    }
//                    .show()
//            }
//            initInput(presenter.getOrgName(organizations, filterOrganizationName, filterOrganizationId)) {
//                if (it.isNullOrBlank()) {
//                    filterOrganizationId = null
//                    filterOrganizationName = null
//                }
//            }
//        }
//    }
//
//    override fun initInterests(interests: Map<InterestNew, List<InterestNew>>) {
//        val themes = interests.keys
//        val selectedTheme = presenter.findInterest(filterTheme, themes)
//        initDropDownView(
//            mBinding.tvTheme,
//            themes,
//            selectedTheme?.name,
//            null,
//            transformKey = { it.name ?: "" },
//            findValue = { it?.id },
//            onVariantChange = { id ->
//                filterTheme = id
//                filterSpec = null
//
//                val specs = presenter.findInterest(id, themes)?.let { interests[it] }
//                initSpecializations(specs, filterSpec)
//            }
//        )
//
//        val specs = selectedTheme?.let { interests[it] }
//        initSpecializations(specs, filterSpec)
//    }
//
//    private fun initSpecializations(interests: List<InterestNew>?, spec: Int?) {
//        if (interests == null) {
//            mBinding.tvSpec.apply {
//                isEnabled = false
//                text = null
//            }
//            mBinding.tilSpec.isEnabled = false
//        } else {
//            mBinding.tvSpec.isEnabled = true
//            mBinding.tilSpec.isEnabled = true
//            val selectedTheme = presenter.findInterest(spec, interests)
//            initDropDownView(
//                mBinding.tvSpec,
//                interests,
//                selectedTheme?.name,
//                null,
//                transformKey = { it.name ?: "" },
//                findValue = { it?.id },
//                onVariantChange = {
//                    filterSpec = it
//                })
//        }
//    }
//
//    private fun clearFiltersView() {
//        mBinding.apply {
//            etName.text = null
//            tvRegion.text = null
//            tvTown.text = null
//            tvFormat.text = null
//            tvOrganization.text = null
//            tvTheme.text = null
//            tvSpec.text = null
//            etStart.text = null
//            etFinish.text = null
//        }
//    }
//
//    fun setFiltersSelected(block: (filters: SearchFilter.EventNew) -> Unit): EventFiltersBottomSheetDialog {
//        onFiltersApply = block
//        return this
//    }
//
//    private fun getEventFilters(): SearchFilter.EventNew {
//        return SearchFilter.EventNew(
//            name = filterName,
//
//            dateStart = filterDateStart,
//            dateFinish = filterDateFinish,
//
//            format = filterFormat,
//            customFormat = filterCustomFormat,
//
//            theme = filterTheme,
//            spec = filterSpec,
//
//            organizationName = filterOrganizationName,
//            organizationId = filterOrganizationId,
//
//            addressRegion = filterRegion,
//            addressTown = filterTown,
//            addressTownType = filterTownType,
//        )
//    }
//
//    override fun onAttachedToWindow() {
//        super.onAttachedToWindow()
//        mvpDelegate.onCreate()
//        mvpDelegate.onAttach()
//    }
//
//    override fun onDetachedFromWindow() {
//        super.onDetachedFromWindow()
//        mvpDelegate.onSaveInstanceState()
//        mvpDelegate.onDetach()
//        mvpDelegate.onDestroyView()
//        mvpDelegate.onDestroy()
//    }
//}