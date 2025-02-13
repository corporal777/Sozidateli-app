package com.example.ui.search.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.example.app.R
import com.example.app.databinding.FragmentChatSearchBinding
import com.example.app.databinding.LayoutFilterUserBinding
import com.example.data.models.InterestNew
import com.example.data.models.SearchFilter
import com.example.data.models.UserDetail
import com.example.extensions.initDropDownView
import com.example.extensions.onScrolled
import com.example.extensions.onTextChanged
import com.example.extensions.updateItem
import com.example.holders.ListSectionNameItem
import com.example.holders.NoDataItem
import com.example.holders.PlaceholderItem
import com.example.holders.UserItem
import com.example.ui.base.BaseVBFragment
import com.example.util.SearchInput
import com.example.util.pagination.PaginationListGroupAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class SearchChatFragment : BaseVBFragment<FragmentChatSearchBinding>(), SearchChatContract.View {

    @InjectPresenter
    lateinit var presenter: SearchChatPresenter

    @Inject
    lateinit var presenterProvider: Provider<SearchChatPresenter>

    @ProvidePresenter
    fun providePresenter(): SearchChatPresenter = presenterProvider.get()

    private var filterDialog: BottomSheetDialog? = null
    private var filterView: View? = null

    private val usersSection by lazy {
        Section().apply {
            setHeader(
                ListSectionNameItem(
                    -300L,
                    getString(R.string.search_contact_section_another)
                )
            )
            setHideWhenEmpty(true)
        }
    }

    private val adapter by lazy {
        PaginationListGroupAdapter<GroupieViewHolder>().apply {
            add(usersSection)
            setOnItemTakeCallback(object : PaginationListGroupAdapter.OnItemTakeCallback {
                override fun onItemTake(position: Int) {
                    presenter.onItemTake(position)
                }
            })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            searchList.adapter = this@SearchChatFragment.adapter
            searchList.onScrolled { dx, dy ->
                presenter.changeAppBarElevation(searchList.computeVerticalScrollOffset())
            }
            etSearch.apply {
                SearchInput(this).apply {
                    setOnTextChange { presenter.onSearchTextChange(it) }
                    setOnTextChangeDone {
                        presenter.onSearchTextChange(it)
                        hideKeyboard(etSearch)
                    }
                }

                onTextChanged {
                    btnClear.isVisible = !it.isNullOrEmpty()
                }

                onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                    clSearch.setBackgroundResource(
                        if (hasFocus) R.drawable.background_search_field_rounded_focused
                        else R.drawable.background_search_field_rounded_normal
                    )
                }
            }
            ivBack.setOnClickListener {
                findNavController().navigateUp()
            }
            swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
        }

        mBinding.btnClear.apply {
            isVisible = !mBinding.etSearch.text.isNullOrEmpty()
            setOnClickListener { mBinding.etSearch.text = null }
        }

        mBinding.btnFilter.setOnClickListener {
            presenter.onFilterClick()
        }
    }


    override fun setUsersData(users: List<UserDetail?>) {
        if (!users.isNullOrEmpty()) {
            usersSection.update(users.map { itemData ->
                if (itemData == null) PlaceholderItem(PlaceholderItem.Type.USER)
                else UserItem(
                    itemData.id,
                    itemData.nameLastName,
                    null,
                    itemData.loadUserImage(),
                    { presenter.onUserClick(itemData) })
            })

        }
        mBinding.swipeToRefresh.isRefreshing = false
    }

    override fun showUser(user: UserDetail) {
        findNavController().navigate(R.id.user_fragment, bundleOf("userId" to user.id.toString()))
    }

    override fun showCurrentUser() {
        findNavController().navigate(R.id.user_profile_fragment)
    }

    override fun showEmptyDataPlaceholder() {
        adapter.updateItem(
            NoDataItem(
                getString(R.string.schedule_my_empty_day_placeholder_title),
                getString(R.string.search_no_data_description)
            )
        )
    }

    override fun setFiltersChosen(isChosen: Boolean) {
        mBinding.btnFilter.apply {
            if (isChosen) setImageResource(R.drawable.ic_filters_selected)
            else setImageResource(R.drawable.ic_filters_new)
        }
    }

    override fun showFilter(filter: SearchFilter.UserNew) {
        val filterContainer =
            (layoutInflater.inflate(R.layout.layout_filter, null) as ViewGroup).apply {
                findViewById<ViewGroup>(R.id.flFilters).apply {
                    filterView = createFilterView(filter)
                    addView(filterView)
                }

                findViewById<View>(R.id.btnApply).setOnClickListener {
                    filterDialog?.dismiss()
                    presenter.onFilterApplyClick()
                }
                findViewById<View>(R.id.btnClear).setOnClickListener { clearFilterView() }
                findViewById<View>(R.id.btnClose).setOnClickListener { filterDialog?.dismiss() }
            }

        filterDialog = BottomSheetDialog(requireContext())
            .apply {
                setContentView(filterContainer)
                val behavior = BottomSheetBehavior.from(filterContainer.parent as View)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                show()
            }
    }

    private fun createFilterView(filter: SearchFilter.UserNew): View {
        return LayoutFilterUserBinding.inflate(LayoutInflater.from(requireContext()), null, false)
            .apply {
                etAddress.apply {
                    setTextWithoutSearch(filter.address)
                    onTextChanged {
                        filter.address = it.toString()
                        if (filter.address.isNullOrBlank()) filter.setAddressFilter(null)
                    }
                    onDataSelectedListener = { filter.setAddressFilter(it) }
                }

                initUserInterests(filter, this)
                lnAgeFilter.isVisible = false
                //initAgeFrom(filter, this)
                //initAgeTo(filter, this)
            }.root
    }

    private fun initUserInterests(filter: SearchFilter.UserNew, binding: LayoutFilterUserBinding) {
        binding.apply {
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
        }
    }

    private fun initAgeFrom(filter: SearchFilter.UserNew, binding: LayoutFilterUserBinding) {
        binding.apply {
            initDropDownView(
                tvAgeFrom,
                presenter.getAgesList(null),
                presenter.getAgesList(null).find { it.toInt() == filter.ageFrom },
                null,
                { it },
                { it },
                {
                    filter.ageFrom = it?.toInt()
                    initAgeTo(filter, binding)
                }
            )
        }
    }

    private fun initAgeTo(filter: SearchFilter.UserNew, binding: LayoutFilterUserBinding) {
        binding.apply {
            initDropDownView(
                tvAgeTo,
                presenter.getAgesList(filter.ageFrom),
                presenter.getAgesList(filter.ageFrom).find { it.toInt() == filter.ageTo },
                null,
                { it },
                { it },
                { filter.ageTo = it?.toInt() }
            )
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

    private fun clearFilterView() {
        filterView?.let {
            LayoutFilterUserBinding.bind(it).apply {
                etAddress.text = null
                tvTheme.text = null
                tvSpec.text = null
                tvAgeFrom.text = null
                tvAgeTo.text = null
            }
        }
    }

    override fun changeAppBarElevation(value: Float) {
        mBinding.appBarLayout.changeAppBarElevation(value)
    }

    override fun binding() = FragmentChatSearchBinding::class.java
    override fun layout() = R.layout.fragment_chat_search


}