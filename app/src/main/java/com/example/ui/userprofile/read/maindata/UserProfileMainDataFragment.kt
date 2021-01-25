package com.example.ui.userprofile.read.maindata

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import androidx.core.text.parseAsHtml
import androidx.core.text.set
import androidx.core.text.toSpannable
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.UserDetail
import com.example.data.models.UserEditDataType
import com.example.data.models.user.User
import com.example.extensions.formatToDefaultDate
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.views.suggestFieldView.DaDataUtil
import com.example.util.ClickableSpan
import com.example.util.USER_DATA_EMPTY
import com.example.util.firstLetterToUppercase
import kotlinx.android.synthetic.main.fragment_user_profile_main_data.*
import me.saket.bettermovementmethod.BetterLinkMovementMethod
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

    override fun onUserUpdated(user: UserDetail?) {
        user ?: return
        tvId.text = getString(R.string.profile_uid, user.id)
        val jObject = DaDataUtil.getLocationJson(requireContext())
        tvLastName.text = user.lastName

        tvName.text = user.name

        tvMiddleName.text = user.getMiddleName()
        val isNoMiddleNameChecked = user.middleName?.value == USER_DATA_EMPTY
        tvMiddleNameTitle.isVisible = !isNoMiddleNameChecked
        tvMiddleName.isVisible = !isNoMiddleNameChecked

        tvBirthday.text = user.birthday?.value?.formatToDefaultDate()

        tvGender.text = user.gender?.firstLetterToUppercase()

        tvAddress.text = DaDataUtil.formatParam(user.address?.country, jObject) ?: DaDataUtil.formatParam(user.address?.country, jObject)

        tvAdditional.text = user.notes

        var filesText = ""
        user.binds?.recommendationFile?.forEach { file ->
            filesText += "<a href='${file.uri}'>${file.name}</a><br>"
        }
        tvFiles.text = filesText.parseAsHtml()
        BetterLinkMovementMethod.linkifyHtml(tvFiles)
                .setOnLinkClickListener { _, url ->
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(url)
                    startActivity(i)
                    true
                }
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
