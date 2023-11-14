package com.example.ui.state.maxNew.interests

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.R
import com.example.data.models.InterestNew
import com.example.data.models.UserInterest
import com.example.holders.OnExpandChange
import com.example.holders.ProfileDataInterestEditItem
import com.example.holders.ProfileExpandableSubtitleGroup
import com.example.ui.state.maxNew.base.BaseMaxStateFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class MaxStatusInterestsFragment : BaseMaxStateFragment<MaxStatusInterestsPresenter>(),
    MaxStatusInterestsContract.View{

    override val title: CharSequence by lazy { getString(R.string.profile_interests) }

    @InjectPresenter
    override lateinit var presenter: MaxStatusInterestsPresenter

    @Inject
    lateinit var presenterProvider: Provider<MaxStatusInterestsPresenter>

    @ProvidePresenter
    fun providePresenter(): MaxStatusInterestsPresenter = presenterProvider.get().apply {
        screen = MaxStatusInterestsFragmentArgs.fromBundle(requireArguments()).screen
    }

    private val onItemExpandChange: OnExpandChange<*> = {
        if (it.isExpanded) {
            val position = getAdapter().getAdapterPosition(it.titleItem)
            (mBinding.recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
                position,
                0
            )
        }
    }

    override fun setInterestsData(interests: Map<InterestNew, List<UserInterest>>) {
        var userInterests = findUserInterests(interests)
        buttonNextEnabled(userInterests.isNotEmpty())

        contentSection.update(interests.map {
            val parent = it.key
            val childList = it.value
            ProfileExpandableSubtitleGroup(
                parent.name ?: "",
                onExpandChange = onItemExpandChange
            ).apply {
                titleItem.badgeCount = childList.count { child -> child.isUserInterest }
                val interestsItems = childList.mapIndexed { index, interest ->
                    ProfileDataInterestEditItem(interest, index != childList.size - 1) {
                        userInterests = findUserInterests(interests)
                        val count = childList.count { child -> child.isUserInterest }
                        titleItem.apply {
                            badgeCount = count
                            notifyChanged(count)
                            buttonNextEnabled(userInterests.isNotEmpty())
                        }
                    }
                }
                addAll(interestsItems)
            }
        })
        onSaveClick = { presenter.onSaveInterestsClick(userInterests) }
    }

    private fun findUserInterests(interests: Map<InterestNew, List<UserInterest>>):List<InterestNew> {
        return interests.values.flatten().filter { item -> item.isUserInterest }
            .map { item -> item.interest }
    }
}