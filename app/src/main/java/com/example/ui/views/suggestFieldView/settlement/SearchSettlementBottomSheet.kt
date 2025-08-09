package com.example.ui.views.suggestFieldView.settlement

//class SearchSettlementBottomSheet(
//    context: Context,
//    val data: String?
//) : BottomSheetDialog(context), SearchSettlementBottomSheetContract.View {
//
//    private val mBinding = BottomSheetEventFormatBinding.inflate(LayoutInflater.from(context))
//    private val mvpDelegate by lazy { MvpDelegate<SearchSettlementBottomSheet>(this) }
//
//    private var onRegionSelected: (region: SearchRegion?) -> Unit = {}
//
//    @InjectPresenter(tag = SETTLEMENT_TAG_VIEW)
//    lateinit var presenter: SearchSettlementBottomSheetPresenter
//
//    @Inject
//    lateinit var presenterProvider: Provider<SearchSettlementBottomSheetPresenter>
//
//    @ProvidePresenter(tag = SETTLEMENT_TAG_VIEW)
//    fun providePresenter(): SearchSettlementBottomSheetPresenter = presenterProvider.get().apply {
//        region = data ?: ""
//    }
//
//    private val simpleTextWatcher = SimpleTextWatcher().setAfterTextChangeRunnable {
//        presenter.onSettlementChange(it.toString())
//        mBinding.btnClear.isVisible = !it.toString().isNullOrEmpty()
//    }
//
//    private val groupAdapter by lazy { GroupAdapter<GroupieViewHolder>() }
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
//            tvBottomSheetLabel.text = context.getString(R.string.search_filter_settlement)
//            ivBack.setOnClickListener { dismiss() }
//            btnClear.apply {
//                isVisible = !etSearch.text.isNullOrEmpty()
//                setOnClickListener { etSearch.text = null }
//            }
//            etSearch.apply {
//                addTextChangedListener(simpleTextWatcher)
//                onFocusChanged { hasFocus ->
//                    clSearch.setBackgroundResource(
//                        if (hasFocus) R.drawable.background_search_field_rounded_focused
//                        else R.drawable.background_search_field_rounded_normal
//                    )
//                }
//            }
//            listContent.adapter = groupAdapter
//        }
//    }
//
//    override fun setSettlements(list: List<SearchRegion?>) {
//        if (list.isEmpty()) {
//            groupAdapter.updateItem(SearchEmptyItem("В выбранном регионе населенных пунктов нет"))
//        } else {
//            groupAdapter.apply {
//                updateAsync(list.map {
//                    if (it == null) {
//                        groupAdapter.updateItem(null)
//                        return
//                    } else SearchItem(it.id, it.name) { r -> presenter.onSettlementSelected(r) }
//                })
//            }
//        }
//    }
//
//
//    override fun performOnItemSelected(item: SearchRegion?) {
//        setTextWithoutSearch(item?.name)
//        onRegionSelected.invoke(item)
//        dismiss()
//    }
//
//
//    fun setTextWithoutSearch(text: String?) {
//        mBinding.etSearch.apply {
//            removeTextChangedListener(simpleTextWatcher)
//            setText(text)
//            addTextChangedListener(simpleTextWatcher)
//        }
//    }
//
//    fun setSettlementSelectedCallback(block: (region: SearchRegion?) -> Unit): SearchSettlementBottomSheet {
//        onRegionSelected = block
//        return this
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
//
//    companion object {
//        private const val SETTLEMENT_TAG_VIEW = "settlement_tag"
//    }
//}