package com.example.ui.tags

import android.view.LayoutInflater
import android.widget.Button
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Tag
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.views.TagChip
import kotlinx.android.synthetic.main.fragment_tags.*
import javax.inject.Inject
import javax.inject.Provider

class TagsFragment : BaseFragment(), TagsContract.View, ToolbarFragment {

    override val title: CharSequence? = null

    @InjectPresenter
    lateinit var presenter: TagsPresenter

    @Inject
    lateinit var presenterProvider: Provider<TagsPresenter>

    @ProvidePresenter
    fun providePresenter(): TagsPresenter = presenterProvider.get()

    override fun setData(tags: List<Tag>) {
        tags.forEach { tag ->
            val chip = TagChip(context).apply {
                text = tag.name
                isChecked = tag.isSelected
                setOnCheckedChangeListener { _, isChecked ->
                    tag.isSelected = isChecked
                    presenter.onTagClick(tag)
                }
            }

            tagGroup.addView(chip)
        }

        LayoutInflater.from(requireContext()).inflate(R.layout.layout_tag_button, tagGroup, true).apply {
            findViewById<Button>(R.id.btnTag).apply {
                text = context.getText(R.string.schedule_clear_tags)
                setOnClickListener { presenter.onClearClick() }
            }
        }
    }

    override fun uselectAllTags() {
        tagGroup.apply {
            for (index in 0 until childCount) {
                val child = getChildAt(index)
                if (child is TagChip) child.isChecked = false
            }
        }
    }

    override fun layout() = R.layout.fragment_tags
}
