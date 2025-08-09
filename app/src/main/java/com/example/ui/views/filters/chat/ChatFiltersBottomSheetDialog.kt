package com.example.ui.views.filters.chat

//class ChatFiltersBottomSheetDialog(
//    val requireContext: Context,
//    filter: SearchFilter.UserNew
//) : BottomSheetDialog(requireContext), ChatFiltersBottomSheetContract.View {
//
//    private val mBinding = BottomSheetChatFiltersBinding.inflate(LayoutInflater.from(requireContext))
//    private val mvpDelegate by lazy { MvpDelegate(this) }
//
//    @InjectPresenter
//    lateinit var presenter: ChatFiltersBottomSheetPresenter
//
//    @Inject
//    lateinit var presenterProvider: Provider<ChatFiltersBottomSheetPresenter>
//
//    @ProvidePresenter
//    fun providePresenter(): ChatFiltersBottomSheetPresenter = presenterProvider.get()
//
//    private var onFiltersApply: (filter: SearchFilter.UserNew) -> Unit = {}
//
//    private var userFilter = filter
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
//                onFiltersApply.invoke(userFilter)
//                dismiss()
//            }
//        }
//    }
//
//    override fun initDaDataAddress() {
//        mBinding.etAddress.apply {
//            setTextWithoutSearch(userFilter.address)
//            onTextChanged {
//                userFilter.address = it.toString()
//                if (userFilter.address.isNullOrBlank()) userFilter.setAddressFilter(null)
//            }
//            onDataSelectedListener = { userFilter.setAddressFilter(it) }
//        }
//    }
//
//
//    override fun initInterests(interests: Map<InterestNew, List<InterestNew>>) {
//        val themes = interests.keys
//        val selectedTheme = presenter.findInterest(userFilter.theme, themes)
//        initDropDownView(
//            mBinding.tvTheme,
//            themes,
//            selectedTheme?.name,
//            null,
//            transformKey = { it.name ?: "" },
//            findValue = { it?.id },
//            onVariantChange = { id ->
//                userFilter.theme = id
//                userFilter.spec = null
//
//                val specs = presenter.findInterest(id, themes)?.let { interests[it] }
//                initSpecializations(specs, userFilter.spec)
//            }
//        )
//
//        val specs = selectedTheme?.let { interests[it] }
//        initSpecializations(specs, userFilter.spec)
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
//                    userFilter.spec = it
//                })
//        }
//    }
//
//    override fun initFormats(formats: List<NewEventFormat>) {}
//
//    private fun clearFiltersView() {
//        mBinding.apply {
//            etAddress.text = null
//            tvTheme.text = null
//            tvSpec.text = null
//        }
//    }
//
//    fun setFiltersSelected(block: (filters: SearchFilter.UserNew) -> Unit): ChatFiltersBottomSheetDialog {
//        onFiltersApply = block
//        return this
//    }
//
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