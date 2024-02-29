package com.example.ui.state.maxNew.work

import com.example.R
import com.example.data.models.UserDetail
import com.example.extensions.updateGroup
import com.example.ui.userprofile.edit.work.items.UserWorksGroup
import com.example.ui.state.maxNew.base.BaseMaxStateFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class MaxStatusWorkFragment : BaseMaxStateFragment<MaxStatusWorkPresenter>(),
    MaxStatusWorkContract.View {

    override val title: CharSequence by lazy { getString(R.string.profile_work_experience) }

    @InjectPresenter
    override lateinit var presenter: MaxStatusWorkPresenter

    @Inject
    lateinit var presenterProvider: Provider<MaxStatusWorkPresenter>

    @ProvidePresenter
    fun providePresenter(): MaxStatusWorkPresenter = presenterProvider.get().apply {
        screen = MaxStatusWorkFragmentArgs.fromBundle(requireArguments()).screen
    }


    override fun setWorkData(user: UserDetail) {
        val work = user.binds?.workExperience
        val dataItem = UserWorksGroup(
            requireContext(),
            user.birthday,
            work
        ) { isEnable -> buttonNextEnabled(isEnable) }

        contentSection.updateGroup(dataItem)
        onSaveClick = {
            if (dataItem.checkDataValid()) presenter.onSaveWorkClick(dataItem.getDataToSave())
        }
    }
}