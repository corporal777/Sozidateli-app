package com.example.ui.views.suggestFieldView.town

//class SearchTownBottomSheet(
//    context: Context,
//    private val selectedRegion: String?,
//    private val selectedType: String,
//    private val title : String = "Город"
//) : BottomSheetDialog(context), SearchTownBottomSheetContract.View {
//
//    private val mBinding = BottomSheetEventFormatBinding.inflate(LayoutInflater.from(context))
//    private val mvpDelegate by lazy { MvpDelegate(this) }
//
//    private var onTownSelected: (region: SearchTown?) -> Unit = {}
//
//    @InjectPresenter(tag = TOWNS_TAG_VIEW)
//    lateinit var presenter: SearchTownBottomSheetPresenter
//
//    @Inject
//    lateinit var presenterProvider: Provider<SearchTownBottomSheetPresenter>
//
//    @ProvidePresenter(tag = TOWNS_TAG_VIEW)
//    fun providePresenter(): SearchTownBottomSheetPresenter = presenterProvider.get().apply {
//        this.region = selectedRegion ?: ""
//        this.type = selectedType
//    }
//
//    private val simpleTextWatcher = SimpleTextWatcher().setAfterTextChangeRunnable {
//        presenter.onTownChange(it.toString())
//        mBinding.apply {
//            btnClear.isVisible = !it.toString().isNullOrEmpty()
//        }
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
//            tvBottomSheetLabel.text = title
//            ivBack.setOnClickListener { dismiss() }
//            btnClear.apply {
//                btnClear.isVisible = !etSearch.text.isNullOrEmpty()
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
//
//        }
//    }
//
//    override fun setTowns(list: List<SearchTown?>) {
//        if (list.isEmpty()){
//            groupAdapter.updateItem(
//                when (presenter.type) {
//                    "event" -> SearchEmptyItem("В населенных пунктах выбранного региона мероприятия не проводятся")
//                    "user" -> SearchEmptyItem("В населенных пунктах выбранного региона пользователей нет")
//                    else -> SearchEmptyItem("В населенных пунктах выбранного региона организаций не зарегистрировано")
//                }
//            )
//        } else {
//            groupAdapter.update(
//                list.map {
//                    if (it == null) {
//                        groupAdapter.updateItem(null)
//                        return
//                    } else SearchItem(it.id, it.name) { town -> presenter.onTownSelected(town) }
//                }
//            )
//        }
//    }
//
//    override fun performOnItemSelected(item: SearchTown?) {
//        setTextWithoutSearch(item?.name)
//        onTownSelected.invoke(item)
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
//    fun setTownSelectedCallback(block: (town: SearchTown?) -> Unit): SearchTownBottomSheet {
//        onTownSelected = block
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
//        private const val TOWNS_TAG_VIEW = "towns_tag"
//    }
//}