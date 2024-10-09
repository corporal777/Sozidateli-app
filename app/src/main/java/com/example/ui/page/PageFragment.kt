package com.example.ui.page

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.navArgs
import com.example.app.R
import com.example.data.models.FileModel
import com.example.app.databinding.FragmentPageBinding
import com.example.extensions.markWon
import com.example.extensions.setOnClickListener
import com.example.holders.DocumentItem
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.base.bottomSheet.BaseBottomSheetFragment
import com.example.ui.event.location.map.MapFragment
import com.example.ui.views.toolbar.ToolbarContent
import com.example.util.setImage
import com.example.util.showCustomTabsBrowser
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class PageFragment(val eventId: String, val pageId: String) :
    BaseBottomSheetFragment<FragmentPageBinding>(), PageContract.View {

    @InjectPresenter(tag = PAGE_FRAGMENT_TAG)
    lateinit var presenter: PagePresenter

    @Inject
    lateinit var presenterProvider: Provider<PagePresenter>

    @ProvidePresenter(tag = PAGE_FRAGMENT_TAG)
    fun providePresenter(): PagePresenter = presenterProvider.get().apply {
        dataEventId = eventId
        dataPageId = pageId
    }

    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            recyclerView.apply {
                adapter = groupAdapter
            }
            ivBack.setOnClickListener {
                dismiss()
            }
        }

    }

    override fun setContent(
        logo: String?,
        contentTitle: String?,
        title: String?,
        content: String?,
        documents: List<FileModel>?
    ) {
        mBinding.tvBottomSheetLabel.text = contentTitle ?: ""

        mBinding.ivLogo.apply {
            clipToOutline = true
            isVisible = !logo.isNullOrEmpty()
            setImage(logo)
        }
        mBinding.tvTitle.apply {
            isVisible = !title.isNullOrBlank()
            text = markWon(requireContext()).toMarkdown(title ?: "")
        }
        mBinding.tvInfo.apply {
            isVisible = !content.isNullOrBlank()
            text = markWon(requireContext()).toMarkdown(content ?: "")
        }

        groupAdapter.update(documents?.map { DocumentItem(it) { presenter.onDocumentClick(it) } }
            ?: emptyList())
    }

    override fun openLinkInBrowser(link: String) = showCustomTabsBrowser(requireContext(), link)

    fun show(fragmentManager: FragmentManager) = show(fragmentManager, PAGE_FRAGMENT_TAG)

    companion object {
        const val PAGE_FRAGMENT_TAG = "page_fragment_tag"
    }

    override fun layout() = R.layout.fragment_page
}
