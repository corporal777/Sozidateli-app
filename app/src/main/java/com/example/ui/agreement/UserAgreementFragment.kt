package com.example.ui.agreement

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import com.example.R
import com.example.databinding.FragmentPageBinding
import com.example.ui.base.BaseFragment
import com.example.ui.views.toolbar.ToolbarContentActionBar
import kotlinx.android.synthetic.main.fragment_page.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class UserAgreementFragment : BaseFragment<FragmentPageBinding>(), UserAgreementContract.View {


    @InjectPresenter
    lateinit var presenter: UserAgreementPresenter

    @Inject
    lateinit var presenterProvider: Provider<UserAgreementPresenter>

    @ProvidePresenter
    fun providePresenter(): UserAgreementPresenter = presenterProvider.get()

    private var toolbarContentActionBar: ToolbarContentActionBar? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ivLogo.isVisible = false
    }

    override fun setContent(content: String) {
        tvInfo.setText(content)
    }

    override fun setTitle(title: String) {
        toolbarContentActionBar?.title = title
    }


    override fun layout() = R.layout.fragment_page
}
