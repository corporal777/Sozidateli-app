package com.example.ui.documents

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Document
import com.example.holders.DocumentItem
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.views.toolbar.ToolbarContentActionBar
import com.example.util.LayoutListWithPlaceholderUtil
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.layout_list_with_placeholder.*
import javax.inject.Inject
import javax.inject.Provider

class DocumentsListFragment : BaseFragment(), DocumentsListContract.View, ToolbarFragment {

    override val title: String
        get() = ""

    @InjectPresenter
    lateinit var presenter: DocumentsListPresenter

    @Inject
    lateinit var presenterProvider: Provider<DocumentsListPresenter>

    @ProvidePresenter
    fun providePresenter(): DocumentsListPresenter = presenterProvider.get().apply {
        DocumentsListFragmentArgs.fromBundle(arguments!!).apply {
            dataEventId = eventId
            dataPageId = pageId
        }
    }

    private lateinit var placeholderUtil: LayoutListWithPlaceholderUtil
    private var toolbarContentActionBar: ToolbarContentActionBar? = null

    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            adapter = this@DocumentsListFragment.groupAdapter
            if (itemDecorationCount == 0) addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(context, VERTICAL))
        }

        placeholderUtil = LayoutListWithPlaceholderUtil(view).apply { setDefault() }
        swipeToRefresh.isEnabled = false
    }

    override fun setData(documents: List<Document>) {
        groupAdapter.update(documents.map { DocumentItem(it) { presenter.onDocumentClick(it) } })
        placeholderUtil.isDataLoad = true
    }

    override fun openLinkInBrowser(link: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        startActivity(browserIntent)
    }

    override fun setTitle(title: String) {
        toolbarContentActionBar?.title = title
    }

    override fun setupToolbarContent(toolbarContentActionBar: ToolbarContentActionBar) {
        super.setupToolbarContent(toolbarContentActionBar)
        this.toolbarContentActionBar = toolbarContentActionBar
    }

    override fun layout() = R.layout.layout_list_with_placeholder
}
