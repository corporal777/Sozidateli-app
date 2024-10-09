package com.example.ui.support.detail

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import com.example.app.R
import com.example.app.databinding.FragmentSupportQuestionDetailBinding
import com.example.extensions.markWon
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.support.newQuestion.SupportQuestionBottomSheet
import com.example.ui.views.toolbar.ToolbarContent
import com.example.util.MarkdownEmphasisPlugin
import com.example.util.showCustomTabsBrowser
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import com.example.extensions.removeUrlUnderline
import javax.inject.Inject
import javax.inject.Provider


class SupportQuestionDetailFragment : BaseFragment<FragmentSupportQuestionDetailBinding>(),
    ToolbarFragment, SupportQuestionDetailContract.View {

    @InjectPresenter
    lateinit var presenter: SupportQuestionDetailPresenter

    @Inject
    lateinit var presenterProvider: Provider<SupportQuestionDetailPresenter>

    @ProvidePresenter
    fun providePresenter(): SupportQuestionDetailPresenter = presenterProvider.get().apply {
        navArgs<SupportQuestionDetailFragmentArgs>().value.also {
            questionTitle = it.supportData.question
            questionAnswer = it.supportData.answer
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            btnSendQuestion.setOnClickListener {
                SupportQuestionBottomSheet(requireContext(), childFragmentManager).show()
            }
        }
    }

    override fun setQuestion(title: String, answer: String) {
        mBinding.apply {
            tvQuestionTitle.text = title
            tvQuestionAnswer.apply {
                highlightColor = ContextCompat.getColor(context, R.color.profile_id_text)
                BetterLinkMovementMethod.linkifyHtml(this)
                    .setOnLinkClickListener { _, url ->
                        showCustomTabsBrowser(context, url)
                        true
                    }

                text = answer.replace("<br>", "\n")
                    .replace("</br>", "\n")
                    //.replace("\u{200C})", ")")
                    .replace("\n‌\n\n", "\n‌")
                    .toMarkdownText()
            }
        }
    }

    private fun String?.toMarkdownText(): CharSequence? {
        if (this.isNullOrBlank()) return null
        val spanned = markWon(
            requireContext(),
            MarkdownEmphasisPlugin(requireContext())
        ).toMarkdown(this)

        return spanned.removeUrlUnderline()
    }


    override fun layout(): Int = R.layout.fragment_support_question_detail
    override val title: CharSequence = ""
    override fun actionIconContainer(view: ViewGroup) {}
    override fun scrollValue(scroll: Int) {}
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {}
}