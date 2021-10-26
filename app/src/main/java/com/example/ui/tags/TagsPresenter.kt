package com.example.ui.tags

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.UserEventData
import com.example.data.models.Tag
import com.example.ui.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class TagsPresenter @Inject constructor(
        userEventData: UserEventData,
        appData: AppData
) : BasePresenter<TagsContract.View>(appData), TagsContract.Presenter {

    private val userEvent = userEventData.userEvent
    var tags = userEvent?.activity.let { it?.groups?.plus(it.tags) }
    var eventId: List<Tag.EventTag>? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.apply {
            if (eventId == null) {
                setData(tags ?: emptyList())
            } else {
                tags = eventId
                setData(tags ?: emptyList())
            }
        }
    }

    override fun onTagClick(tag: Tag) {

    }

    override fun onClearClick() {
        tags?.forEach { it.isSelected = false }
        viewState.uselectAllTags()
    }
}