package com.example.ui.userprofile.edit.work

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.app.R
import com.example.data.models.UserDetail
import com.example.app.databinding.FragmentEditWorkFragmentBinding
import com.example.extensions.findGroupBy
import com.example.extensions.updateGroup
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.userprofile.base.BaseUserProfileEditFragment
import com.example.ui.userprofile.edit.work.items.UserWorksGroup
import com.example.ui.views.toolbar.ToolbarContent
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
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