package com.example.ui.page

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Document
import com.example.data.models.FileModel
import com.example.holders.DocumentItem
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.views.toolbar.ToolbarContentActionBar
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_page.*
import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter
import javax.inject.Inject
import javax.inject.Provider

class PageFragment : BaseFragment(), PageContract.View, ToolbarFragment {

    override val title: String? = null

    @InjectPresenter
    lateinit var presenter: PagePresenter

    @Inject
    lateinit var presenterProvider: Provider<PagePresenter>

    @ProvidePresenter
    fun providePresenter(): PagePresenter = presenterProvider.get().apply {
        args.apply {
            dataEventId = eventId
            dataPageId = pageId
        }
    }

    private val args: PageFragmentArgs by navArgs()

    private lateinit var toolbarContentActionBar: ToolbarContentActionBar

    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            adapter = groupAdapter
        }
    }

    override fun setContent(
            logo: String?,
            contentTitle: String,
            title: String?,
            content: String?,
            documents: List<FileModel>?
    ) {
        toolbarContentActionBar.title = contentTitle

        ivLogo.apply {
            clipToOutline = true
            val visible = !logo.isNullOrEmpty()
            if (visible) Picasso.get().load(logo).into(this)
            isVisible = visible
        }

        tvTitle.apply {
            if (title.isNullOrBlank()) {
                isVisible = false
            } else {
                isVisible = true
                setHtml(title, HtmlHttpImageGetter(this))
            }
        }

        tvInfo.apply {
            if (content.isNullOrBlank()) {
                isVisible = false
            } else {
                isVisible = true
                setHtml(content, HtmlHttpImageGetter(this))
            }
        }

        groupAdapter.update(documents?.map { DocumentItem(it) { presenter.onDocumentClick(it) } }
                ?: emptyList())
    }

    override fun openLinkInBrowser(link: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        startActivity(browserIntent)
    }

    override fun setupToolbarContent(toolbarContentActionBar: ToolbarContentActionBar) {
        super.setupToolbarContent(toolbarContentActionBar)
        this.toolbarContentActionBar = toolbarContentActionBar
    }

    override fun layout() = R.layout.fragment_page
}
