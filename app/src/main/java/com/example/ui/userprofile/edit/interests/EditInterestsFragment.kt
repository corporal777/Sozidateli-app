package com.example.ui.userprofile.edit.interests

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app.R
import com.example.data.models.InterestNew
import com.example.data.models.UserInterest
import com.example.holders.OnExpandChange
import com.example.holders.PlaceholderItem
import com.example.holders.ProfileDataInterestEditItem
import com.example.holders.ProfileExpandableSubtitleGroup
import com.example.ui.userprofile.base.BaseUserProfileEditFragment
import com.example.ui.userprofile.edit.contacts.EditContactsPresenter
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class EditInterestsFragment : BaseUserProfileEditFragment(), EditInterestsContract.View {

    @InjectPresenter
    lateinit var presenter: EditInterestsPresenter

    @Inject
    lateinit var presenterProvider: Provider<EditInterestsPresenter>

    @ProvidePresenter
    fun providePresenter(): EditInterestsPresenter = presenterProvider.get()

    private val onItemExpandChange: OnExpandChange<*> = {
        if (it.isExpanded) {
            val position = groupAdapter.getAdapterPosition(it.titleItem)
            val manager = mBinding.recyclerView.layoutManager as LinearLayoutManager
            manager.scrollToPositionWithOffset(position, 0)
        }
    }

    override fun setPlaceholder() {
        groupAdapter.update(List(7) { PlaceholderItem(PlaceholderItem.Type.INTERESTS) })
    }

    override fun setInterestsData(interests: Map<InterestNew, List<UserInterest>>) {
        var userInterests = findUserInterests(interests)
        if (userInterests.isNullOrEmpty()) mBinding.btnSave.isEnabled = false

        groupAdapter.update(interests.map {
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
                        }
                        if (count > 0) mBinding.btnSave.isEnabled = true
                    }
                }
                addAll(interestsItems)
            }
        })

        onSaveClick = {
            showEditWarning(
                presenter.getBaseUserState(),
                presenter.getMaxUserState(), false, userInterests.isEmpty()
            ) {
                presenter.onSaveInterestsClick(userInterests)
            }
        }
    }

    private fun findUserInterests(interests: Map<InterestNew, List<UserInterest>>): List<InterestNew> {
        return interests.values.flatten().filter { item -> item.isUserInterest }
            .map { item -> item.interest }
    }


    override val title: CharSequence by lazy { getString(R.string.profile_interests) }
}