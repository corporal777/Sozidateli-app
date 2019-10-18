package com.example.ui.page

import androidx.core.view.isVisible
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.views.toolbar.ToolbarContentActionBar
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_page.*
import javax.inject.Inject
import javax.inject.Provider

class PageFragment : BaseFragment(), PageContract.View, ToolbarFragment {

    override val title: String
        get() = ""

    @InjectPresenter
    lateinit var presenter: PagePresenter

    @Inject
    lateinit var presenterProvider: Provider<PagePresenter>

    @ProvidePresenter
    fun providePresenter(): PagePresenter = presenterProvider.get().apply {
        PageFragmentArgs.fromBundle(arguments!!).apply {
            dataEventId = eventId
            dataPageId = pageId
        }
    }

    private var toolbarContentActionBar: ToolbarContentActionBar? = null

    override fun setContent(logo: String?, content: String) {
        ivLogo.apply {
            val visible = !logo.isNullOrEmpty()
            if (visible) Picasso.get().load(logo).into(this)
            isVisible = visible
        }

        tvInfo.setHtml(content)
    }

    override fun setTitle(title: String) {
        toolbarContentActionBar?.title = title
    }

    override fun setupToolbarContent(toolbarContentActionBar: ToolbarContentActionBar) {
        super.setupToolbarContent(toolbarContentActionBar)
        this.toolbarContentActionBar = toolbarContentActionBar
    }

    override fun layout() = R.layout.fragment_page
}
