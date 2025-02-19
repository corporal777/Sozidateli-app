package com.example.ui.organizations.members

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import com.example.adapters.UserPagingAdapter
import com.example.adapters.UserPagingAdapter.Companion.withLoadStateAdapters
import com.example.adapters.UserPlaceholderAdapter
import com.example.app.R
import com.example.app.databinding.LayoutListBinding
import com.example.data.models.UserDetail
import com.example.ui.base.BaseToolbarFragment
import com.example.ui.user.UserFragmentArgs
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class OrganizationMembersFragment : BaseToolbarFragment<LayoutListBinding>(), OrganizationMembersContract.View {

    @InjectPresenter
    lateinit var presenter: OrganizationMembersPresenter

    @Inject
    lateinit var presenterProvider: Provider<OrganizationMembersPresenter>

    @ProvidePresenter
    fun providePresenter(): OrganizationMembersPresenter = presenterProvider.get().apply {
        organizationId = OrganizationMembersFragmentArgs.fromBundle(requireArguments()).organizationId
    }



    private val pagingAdapter by lazy(LazyThreadSafetyMode.NONE) {
        UserPagingAdapter({ presenter.onMemberClick(it) }, { presenter.onAddUserFavoriteCLick(it) })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            recyclerView.adapter = pagingAdapter
                .withLoadStateAdapters(
                    UserPlaceholderAdapter(9),
                    UserPlaceholderAdapter(1)
                ) {  }
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

    override fun showUser(userId: String) {
        val args = UserFragmentArgs.Builder(userId).build().toBundle()
        findNavController().navigate(R.id.user_fragment, args)
    }

    override fun showCurrentUser(userId: String) {
        findNavController().navigate(R.id.user_profile_fragment)
    }


    override fun binding() = LayoutListBinding::class.java
    override fun layout() = R.layout.layout_list
    override val title: CharSequence by lazy { getString(R.string.organization_members) }
    override fun scrollingView(): View = mBinding.recyclerView
}
