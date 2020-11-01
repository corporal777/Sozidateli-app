package com.example.ui.userprofile.read.maindata

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import androidx.core.text.set
import androidx.core.text.toSpannable
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.UserEditDataType
import com.example.data.models.user.User
import com.example.extensions.formatToDefaultDate
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.util.ClickableSpan
import com.example.util.USER_MIDDLE_NAME_EMPTY
import kotlinx.android.synthetic.main.fragment_user_profile_main_data.*
import setOnClickListener
import javax.inject.Inject
import javax.inject.Provider

class UserProfileMainDataFragment : BaseFragment(), UserProfileMainDataContract.View, ToolbarFragment {

    override val title: String?
        get() = getString(R.string.user_profile_main_info)

    override fun layout() = R.layout.fragment_user_profile_main_data

    @InjectPresenter
    lateinit var presenter: UserProfileMainDataPresenter

    @Inject
    lateinit var presenterProvider: Provider<UserProfileMainDataPresenter>

    @ProvidePresenter
    fun providePresenter(): UserProfileMainDataPresenter = presenterProvider.get()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnEdit.setOnClickListener(presenter::onEditClick)
    }

    override fun onUserUpdated(user: User?) {
        user ?: return
        tvId.text = getString(R.string.profile_uid, user.user_id)

        tvLastName.text = user.user_last_name

        tvName.text = user.user_name

        tvMiddleName.text = user.getMiddleName()
        val isNoMiddleNameChecked = user.user_middle_name == USER_MIDDLE_NAME_EMPTY
        tvMiddleNameTitle.isVisible = !isNoMiddleNameChecked
        tvMiddleName.isVisible = !isNoMiddleNameChecked

        tvBirthday.text = user.user_birthday?.formatToDefaultDate()

        tvGender.text = user.user_gender

        tvAddress.text = user.user_short_address ?: user.user_address

        tvAdditional.text = user.user_notes

        val filesText = user.attached_recomendation_files?.joinTo(SpannableStringBuilder(), "\n") { file ->
            file.getReadableName().toSpannable().apply {
                set(0, this.length, ClickableSpan {
                    downloadFile(file.url)
                })
            }
        }
        tvFiles.text = filesText
    }

    private fun downloadFile(file: String?) {
        val uri = file?.let { Uri.parse(it) } ?: return
        try {
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        } catch (e: ActivityNotFoundException) {
            showRequestErrorMessage()
        }
    }

    override fun showEdit() {
        findNavController().navigate(UserProfileMainDataFragmentDirections.toEdit(UserEditDataType.PERSONAL))
    }
}
