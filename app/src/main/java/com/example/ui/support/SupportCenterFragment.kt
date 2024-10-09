package com.example.ui.support

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.example.app.R
import com.example.data.models.SupportData
import com.example.app.databinding.FragmentSupportCenterBinding
import com.example.holders.OnGroupExpandChange
import com.example.holders.redesign.EventPageItem
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.support.detail.SupportQuestionDetailFragmentArgs
import com.example.ui.support.items.SupportFooterItem
import com.example.ui.support.items.SupportHeaderItem
import com.example.ui.support.items.SupportQuestionExpandableTitleGroup
import com.example.ui.views.toolbar.ToolbarContent
import com.example.util.setTint
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class SupportCenterFragment : BaseFragment<FragmentSupportCenterBinding>(), ToolbarFragment,
    SupportCenterContract.View {

    @InjectPresenter
    lateinit var presenter: SupportCenterPresenter

    @Inject
    lateinit var presenterProvider: Provider<SupportCenterPresenter>

    @ProvidePresenter
    fun providePresenter(): SupportCenterPresenter = presenterProvider.get()


    private val questionsSection by lazy {
        Section().apply {
            setHeader(SupportHeaderItem(requireContext(), { presenter.onSearchClick() }, { }))
            setFooter(SupportFooterItem(requireContext(), childFragmentManager))
        }
    }
    private val groupAdapter by lazy {
        GroupAdapter<GroupieViewHolder>().apply {
            add(questionsSection)
        }
    }

    private val onItemExpandChange: OnGroupExpandChange<*> = {
        if (it.isExpanded) {
            val position = groupAdapter.getAdapterPosition(it.titleItem)
            val mSmoothScroller: RecyclerView.SmoothScroller =
                object : LinearSmoothScroller(requireContext()) {
                    override fun getVerticalSnapPreference(): Int {
                        return SNAP_TO_START
                    }
                }
            val mLayoutManager = mBinding.supportList.layoutManager as LinearLayoutManager
            mSmoothScroller.targetPosition = position
            mLayoutManager.startSmoothScroll(mSmoothScroller)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.supportList.apply {
            adapter = groupAdapter
            doOnPreDraw { startPostponedEnterTransition() }
        }
    }

    override fun setQuestions(questions: Map<String, List<SupportData>>) {
        questionsSection.update(questions.map {
            SupportQuestionExpandableTitleGroup(
                it.key,
                onExpandChange = onItemExpandChange
            ).apply {
                it.value.forEachIndexed { index, s ->
                    add(EventPageItem(index, s.question) { presenter.onQuestionClick(s) })
                }
            }
        })
    }

    override fun showSupportQuestionAnswer(data: SupportData) {
        val args = SupportQuestionDetailFragmentArgs.Builder(data).build().toBundle()
        findNavController().navigate(R.id.supportDetailFragment, args)
    }

    override fun showSupportSearch() {
        findNavController().navigate(R.id.supportSearchFragment)
    }

    override fun animationType(): AnimType = AnimType.AXIS
    override fun layout(): Int = R.layout.fragment_support_center
    override val title: CharSequence by lazy { getString(R.string.support_directory) }
    override fun actionIconContainer(view: ViewGroup) {}
    override fun scrollValue(scroll: Int) {}
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {}
}