package com.example.ui.support.detail

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import com.example.R
import com.example.databinding.FragmentSupportQuestionDetailBinding
import com.example.extensions.markWon
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.support.newQuestion.SupportQuestionBottomSheet
import com.example.ui.views.toolbar.ToolbarContent
import com.example.util.setTint
import com.example.util.showCustomTabsBrowser
import com.yydcdut.markdown.MarkdownConfiguration
import com.yydcdut.markdown.MarkdownProcessor
import com.yydcdut.markdown.syntax.text.TextFactory
import `in`.uncod.android.bypass.Bypass
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.MarkwonSpansFactory
import io.noties.markwon.RenderProps
import io.noties.markwon.SpanFactory
import io.noties.markwon.core.CoreProps
import io.noties.markwon.core.spans.OrderedListItemSpan
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import org.commonmark.node.ListItem
import removeUrlUnderline
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
        val spanned = markWon(requireContext()).toMarkdown(this)
        return spanned.removeUrlUnderline()
    }

    private fun toRxMarkdown(text : String): CharSequence? {
        val config = MarkdownConfiguration.Builder(requireContext())
            .setLinkFontColor(ContextCompat.getColor(requireContext(), R.color.main_brown_color_new))
            .showLinkUnderline(false)
            .build()

        val markdownProcessor = MarkdownProcessor(requireContext())
        markdownProcessor.factory(TextFactory.create())
        markdownProcessor.config(config)
        return markdownProcessor.parse(text)
    }

    override fun layout(): Int = R.layout.fragment_support_question_detail
    override val title: CharSequence = ""
    override fun actionIconContainer(view: ViewGroup) {}
    override fun scrollValue(scroll: Int) {}
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {}
}