package com.example.ui.page

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.navigation.fragment.navArgs
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.FileModel
import com.example.databinding.FragmentPageBinding
import com.example.holders.DocumentItem
import com.example.ui.base.BaseFragmentNew
import com.example.ui.views.toolbar.SimpleTitleToolbar
import com.example.util.markWon
import com.example.util.showCustomTabsBrowser
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import onScrolled
import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter
import javax.inject.Inject
import javax.inject.Provider

class PageFragment : BaseFragmentNew<FragmentPageBinding>(), PageContract.View, SimpleTitleToolbar {

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

    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            recyclerView.apply {
                adapter = groupAdapter
            }
            scrollContainer.onScrolled { scrollY, oldScrollY, _, _ ->
                presenter.changeAppBarElevation(scrollY - oldScrollY)
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
        setToolbarTitleAndIcon(contentTitle)
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

    override fun openLinkInBrowser(link: String) {
        showCustomTabsBrowser(requireContext(), link)
//        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
//        startActivity(browserIntent)
    }


    override fun layout() = R.layout.fragment_page
}
