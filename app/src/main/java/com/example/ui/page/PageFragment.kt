package com.example.ui.page

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import com.example.R
import com.example.data.models.FileModel
import com.example.databinding.FragmentPageBinding
import com.example.extensions.markWon
import com.example.holders.DocumentItem
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.views.toolbar.ToolbarContent
import com.example.util.showCustomTabsBrowser
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter
import javax.inject.Inject
import javax.inject.Provider

class PageFragment : BaseFragment<FragmentPageBinding>(), PageContract.View, ToolbarFragment {

    private lateinit var toolbarContent: ToolbarContent
    private val args: PageFragmentArgs by navArgs()

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

    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            recyclerView.apply {
                adapter = groupAdapter
            }
        }

    }

    override fun setContent(
        logo: String?,
        contentTitle: String,
        title: String?,
        content: String?,
        documents: List<FileModel>?
    ) {
        toolbarContent.setToolbarTitle(contentTitle)
        mBinding.ivLogo.apply {
            clipToOutline = true
            val visible = !logo.isNullOrEmpty()
            if (visible) Picasso.get().load(logo).into(this)
            isVisible = visible
        }

        mBinding.tvTitle.apply {
            if (title.isNullOrBlank()) {
                isVisible = false
            } else {
                isVisible = true
                //markWon(requireContext()).setMarkdown(this, title)
                setHtml(title, HtmlHttpImageGetter(this))
            }
        }

        mBinding.tvInfo.apply {
            if (content.isNullOrBlank()) {
                isVisible = false
            } else {
                isVisible = true
                markWon(requireContext()).setMarkdown(this, content)
                //setHtml(content, HtmlHttpImageGetter(this))
            }
        }

        groupAdapter.update(documents?.map { DocumentItem(it) { presenter.onDocumentClick(it) } }
            ?: emptyList())
    }

    override fun openLinkInBrowser(link: String) = showCustomTabsBrowser(requireContext(), link)


    override fun layout() = R.layout.fragment_page
    override val title: CharSequence = ""
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {
        this.toolbarContent = toolbarContent
    }
    override fun actionIconContainer(view: ViewGroup) {}
    override fun scrollValue(scroll: (value: Int) -> Unit) {}
}
