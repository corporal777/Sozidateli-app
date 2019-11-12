package com.example.ui.tags

import com.arellomobile.mvp.InjectViewState
import com.example.data.UserEventData
import com.example.data.models.Tag
import com.example.ui.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class TagsPresenter @Inject constructor(
        userEventData: UserEventData
) : BasePresenter<TagsContract.View>(), TagsContract.Presenter {

    private val userEvent = userEventData.userEvent!!
    private val tags = userEvent.activity.let { it.groups.plus(it.tags) }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.apply {
            setData(tags)
            setTitle(userEvent.eventInfo.event.name)
        }
    }

    override fun onTagClick(tag: Tag) {

    }
}