package com.example.ui.search.chat

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import com.example.adapters.user.UserPagingAdapter
import com.example.adapters.user.UserPagingAdapter.Companion.withLoadStateAdapters
import com.example.adapters.user.UserPlaceholderAdapter
import com.example.app.R
import com.example.app.databinding.FragmentChatSearchBinding
import com.example.data.models.SearchFilter
import com.example.data.models.UserDetail
import com.example.extensions.isVisibleAnim
import com.example.extensions.onFocusChanged
import com.example.extensions.onScrolled
import com.example.extensions.onTextChanged
import com.example.extensions.setFiltersBackground
import com.example.ui.base.BaseVBFragment
import com.example.ui.views.filters.chat.ChatFiltersBottomSheetDialog
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


    private val pagingAdapter by lazy(LazyThreadSafetyMode.NONE) {
        UserPagingAdapter({ presenter.onUserClick(it) }, { presenter.onUserActionCLick(it) })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            searchList.apply {
                adapter = pagingAdapter.withLoadStateAdapters(
                    UserPlaceholderAdapter(9),
                    UserPlaceholderAdapter(1)
                ) { setEmptyDataPlaceholder(it) }

                setEmptyDataPlaceholder(isEmptyData)
                onScrolled { _, _ -> presenter.onChangeOffset(computeVerticalScrollOffset()) }
            }

            etSearch.apply {
                onTextChanged {
                    btnClear.isVisible = !it.isNullOrEmpty()
                    presenter.onSearchTextChange(it.toString())
                }
                onFocusChanged { hasFocus ->
                    clSearch.setBackgroundResource(
                        if (hasFocus) R.drawable.background_search_field_rounded_focused
                        else R.drawable.background_search_field_rounded_normal
                    )
                }
            }
            btnClear.apply {
                isVisible = !etSearch.text.isNullOrEmpty()
                setOnClickListener { mBinding.etSearch.text = null }
            }
            btnFilter.setOnClickListener { presenter.onFilterClick() }
            ivBack.setOnClickListener { navigateUp() }
            swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
        }
    }



    override fun setData(data: PagingData<UserDetail>) {
        pagingAdapter.submitData(lifecycle, data)
        mBinding.swipeToRefresh.isRefreshing = false
    }

    override fun updateUser(user: UserDetail) {
        pagingAdapter.updateUserFavorite(user)
    }


    override fun showFilter(filter: SearchFilter.UserNew) {
        ChatFiltersBottomSheetDialog(requireContext(), filter)
            .setFiltersSelected { presenter.onFilterApplyClick(it) }
            .show()
    }


    override fun showUser(user: UserDetail) {
        findNavController().navigate(R.id.user_fragment, bundleOf("userId" to user.id.toString()))
    }

    override fun showCurrentUser() {
        findNavController().navigate(R.id.user_profile_fragment)
    }

    override fun setEmptyDataPlaceholder(show: Boolean) {
        super.setEmptyDataPlaceholder(show)
        mBinding.tvEmptyDataTitle.isVisibleAnim = show
        mBinding.tvEmptyDataDescription.isVisibleAnim = show
    }

    override fun setFiltersChosen(isChosen: Boolean) {
        mBinding.btnFilter.setFiltersBackground(isChosen)
    }

    override fun changeAppBarElevation(value: Float) {
        mBinding.cvAppBar.cardElevation = if (value <= 10f) value else 10f
    }

    override fun animationType(): AnimType = AnimType.FADE
    override fun binding() = FragmentChatSearchBinding::class.java
    override fun layout() = R.layout.fragment_chat_search
}