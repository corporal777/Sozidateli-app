package com.example.ui.userprofile.edit.work

import com.example.app.R
import com.example.data.models.UserDetail
import com.example.extensions.updateGroup
import com.example.ui.userprofile.base.BaseUserProfileEditFragment
import com.example.ui.userprofile.edit.work.items.UserWorksGroup
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class EditWorksFragment : BaseUserProfileEditFragment(), EditWorksContract.View {

    @InjectPresenter
    lateinit var presenter: EditWorksPresenter

    @Inject
    lateinit var presenterProvider: Provider<EditWorksPresenter>

    @ProvidePresenter
    fun providePresenter(): EditWorksPresenter = presenterProvider.get()


    override fun setWorkData(user: UserDetail) {
        val work = user.binds?.workExperience
        val dataItem = UserWorksGroup(
            requireContext(),
            user.birthday,
            work
        ) { isEnable -> buttonSaveEnabled(isEnable) }

        onSaveClick = {
            if (dataItem.checkDataValid()) presenter.onSaveWorkClick(dataItem.getDataToSave())
        }
        groupAdapter.updateGroup(dataItem)
    }


    override val title: CharSequence by lazy { getString(R.string.profile_work_experience) }
}