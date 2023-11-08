package com.example.ui.support.detail

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.navigation.fragment.navArgs
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.SupportData
import com.example.databinding.FragmentSupportQuestionDetailBinding
import com.example.extensions.updateItem
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.support.SupportCenterPresenter
import com.example.ui.support.items.SupportFooterItem
import com.example.ui.support.items.SupportHeaderItem
import com.example.ui.views.toolbar.ToolbarContent
import com.example.util.setTint
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import javax.inject.Inject
import javax.inject.Provider

class SupportQuestionDetailFragment : BaseFragment<FragmentSupportQuestionDetailBinding>(),
    ToolbarFragment,
    SupportQuestionDetailContract.View {

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

    private val dataSection by lazy { Section() }
    private val footerSection by lazy {
        Section().apply { updateItem(SupportFooterItem(requireContext(), childFragmentManager)) }
    }
    private val groupAdapter by lazy {
        GroupAdapter<GroupieViewHolder>().apply {
            add(dataSection)
            add(footerSection)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.questionList.apply {
            adapter = groupAdapter
        }
    }

    override fun setQuestion(title: String, answer: String) {
        dataSection.updateItem(SupportQuestionAnswerItem(requireContext(), title, answer))
    }

    override fun layout(): Int = R.layout.fragment_support_question_detail
    override val title: CharSequence = ""
    override fun actionIconContainer(view: ViewGroup) {}
    override fun scrollValue(scroll: (value: Int) -> Unit) {}
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {
        toolbarContent.getBackButton().setTint(R.color.main_brown_color_new)
    }

}