package com.example.ui.userprofile.read.maindata

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.text.parseAsHtml
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.UserDetail
import com.example.data.models.UserEditDataType
import com.example.databinding.FragmentUserProfileMainDataBinding
import com.example.extensions.formatToDefaultDate
import com.example.interfaces.ToolbarFragmentNew
import com.example.ui.base.BaseFragmentNew
import com.example.ui.views.suggestFieldView.address.DaDataUtil
import com.example.ui.views.toolbar.ToolbarContent
import com.example.util.GENDER_FEMALE
import com.example.util.GENDER_MALE
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import onScrolled
import setOnClickListener
import javax.inject.Inject
import javax.inject.Provider

class UserProfileMainDataFragment : BaseFragmentNew<FragmentUserProfileMainDataBinding>(),
    UserProfileMainDataContract.View, ToolbarFragmentNew {

    override fun layout() = R.layout.fragment_user_profile_main_data

    @InjectPresenter
    lateinit var presenter: UserProfileMainDataPresenter

    @Inject
    lateinit var presenterProvider: Provider<UserProfileMainDataPresenter>

    @ProvidePresenter
    fun providePresenter(): UserProfileMainDataPresenter = presenterProvider.get()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            nestedScrollView.onScrolled { scrollY, oldScrollY, scrollX, oldScrollX ->
                presenter.changeAppBarElevation(scrollY - oldScrollY)
            }
            btnEdit.setOnClickListener(presenter::onEditClick)
        }
    }

    override fun onUserUpdated(user: UserDetail?, state: String) {
        user ?: return
        val jObject = DaDataUtil.getLocationJson(requireContext())

        mBinding.apply {
            tvBirthday.text = user.birthday?.value?.formatToDefaultDate()

            tvGender.text = setGender(user)

            tvAddress.text = DaDataUtil.formatParam(user.address?.country, jObject)
                ?: DaDataUtil.formatParam(user.address?.country, jObject)

            tvAdditional.text = user.notes?.value

            var filesText = ""
            user.binds?.recommendationFile?.forEach { file ->
                filesText += "<a href='${file.uri}'>${file.name}</a><br>"
            }
            tvFiles.text = filesText.parseAsHtml()
        }
        BetterLinkMovementMethod.linkifyHtml(mBinding.tvFiles)
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

    private fun setGender(user: UserDetail): String {
        return when (user.gender?.value) {
            GENDER_MALE -> requireContext().getString(R.string.profile_gender_male)
            GENDER_FEMALE -> requireContext().getString(R.string.profile_gender_female)
            else -> ""
        }
    }


    override val title: CharSequence by lazy { getString(R.string.user_profile_main_info) }
    override val actionIconHidden: Boolean = true
    override val actionIcon: Drawable? = null
    override fun actionIconClick() {}
    override fun toolbarTitleClick() {}
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {}
}
