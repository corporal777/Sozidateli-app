package com.example.ui.tags

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Tag
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.views.TagChip
import com.example.ui.views.toolbar.ToolbarContentActionBar
import kotlinx.android.synthetic.main.fragment_tags.*
import javax.inject.Inject
import javax.inject.Provider

class TagsFragment : BaseFragment(), TagsContract.View, ToolbarFragment {

    override val title: CharSequence
        get() = ""

    @InjectPresenter
    lateinit var presenter: TagsPresenter

    @Inject
    lateinit var presenterProvider: Provider<TagsPresenter>

    @ProvidePresenter
    fun providePresenter(): TagsPresenter = presenterProvider.get()

    private lateinit var toolbarContentActionBar: ToolbarContentActionBar

    override fun setData(tags: List<Tag>) {
        tags.forEach { tag ->
            val chip = TagChip(context).apply {
                text = tag.name
                isCheckable = true
                isChecked = tag.isSelected
                setOnCheckedChangeListener { _, isChecked ->
                    tag.isSelected = isChecked
                    presenter.onTagClick(tag)
                }
            }

            tagGroup.addView(chip)
        }
    }

    override fun setTitle(title: String) {
        toolbarContentActionBar.title = title
    }

    override fun setupToolbarContent(toolbarContentActionBar: ToolbarContentActionBar) {
        super.setupToolbarContent(toolbarContentActionBar)
        this.toolbarContentActionBar = toolbarContentActionBar
    }

    override fun layout() = R.layout.fragment_tags
}
