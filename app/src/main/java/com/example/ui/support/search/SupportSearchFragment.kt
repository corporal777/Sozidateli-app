package com.example.ui.support.search

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.example.R
import com.example.data.models.SupportData
import com.example.databinding.FragmentSupportSearchBinding
import com.example.ui.base.BaseFragment
import com.example.ui.support.detail.SupportQuestionDetailFragmentArgs
import com.example.ui.support.items.SupportFooterItem
import com.example.ui.views.suggestFieldView.region.SearchEmptyItem
import com.example.ui.views.suggestFieldView.region.SearchItem
import com.example.util.SearchInput
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class SupportSearchFragment : BaseFragment<FragmentSupportSearchBinding>(),
    SupportSearchContract.View {

    @InjectPresenter
    lateinit var presenter: SupportSearchPresenter

    @Inject
    lateinit var presenterProvider: Provider<SupportSearchPresenter>

    @ProvidePresenter
    fun providePresenter(): SupportSearchPresenter = presenterProvider.get()


    private val questionsSection by lazy { Section() }
    private val groupAdapter by lazy {
        GroupAdapter<GroupieViewHolder>().apply {
            add(questionsSection)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            supportList.adapter = groupAdapter
            btnClear.apply {
                isVisible = !etSearch.text.isNullOrEmpty()
                setOnClickListener { etSearch.text = null }
            }
            tvCancel.setOnClickListener {
                findNavController().navigateUp()
            }
            etSearch.apply {
                SearchInput(this).apply {
                    setOnTextChange {
                        presenter.onSearchTextChange(it)
                        btnClear.isVisible = !it.isNullOrEmpty()
                    }
                    setOnTextChangeDone {
                        hideKeyboard(etSearch)
                        presenter.onSearchTextSubmit(it)
                    }
                }
                onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                    clSearch.setBackgroundResource(
                        if (hasFocus) R.drawable.background_search_field_rounded_focused
                        else R.drawable.background_search_field_rounded_normal
                    )
                }
            }
        }
    }

    override fun setQuestions(data: List<SupportData>) {
        questionsSection.update(
            if (data.isEmpty()) {
                listOf(SearchEmptyItem(getString(R.string.support_search_data_not_found)))
            } else {
                data.map { SearchItem(it.id, it.question) { q -> presenter.onQuestionClick(q) } }
            }.plus(SupportFooterItem(requireContext(), childFragmentManager))
        )
    }

    override fun showSupportQuestionAnswer(data: SupportData) {
        val args = SupportQuestionDetailFragmentArgs.Builder(data).build().toBundle()
        findNavController().navigate(R.id.supportDetailFragment, args)
    }

    override fun layout(): Int = R.layout.fragment_support_search


}