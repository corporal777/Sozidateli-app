package com.example.ui.banned

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.app.R
import com.example.app.databinding.LayoutListBinding
import com.example.data.models.UserChat
import com.example.ui.base.BaseToolbarFragment
import com.example.ui.user.UserFragmentArgs
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class BannedFragment : BaseToolbarFragment<LayoutListBinding>(), BannedContract.View {

    @InjectPresenter
    lateinit var presenter: BannedPresenter

    @Inject
    lateinit var presenterProvider: Provider<BannedPresenter>

    @ProvidePresenter
    fun providePresenter(): BannedPresenter = presenterProvider.get()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            recyclerView.apply {

            }
            swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
        }
    }

    override fun setItems(userChats: List<UserChat?>) {


        mBinding.swipeToRefresh.isRefreshing = false
    }

    override fun openUserInfo(userId: String) {
        val args = UserFragmentArgs.Builder(userId).build().toBundle()
        findNavController().navigate(R.id.user_fragment, args)
    }

    override fun layout() = R.layout.layout_list
    override fun binding() = LayoutListBinding::class.java
    override val title: CharSequence by lazy { getString(R.string.profile_banned) }
    override fun scrollingView(): View = mBinding.recyclerView
}
