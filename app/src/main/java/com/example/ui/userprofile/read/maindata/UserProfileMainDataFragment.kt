package com.example.ui.userprofile.read.maindata

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.R
import com.example.data.models.UserDetail
import com.example.data.models.UserEditDataType
import com.example.databinding.FragmentUserProfileMainDataBinding
import com.example.extensions.formatToDefaultDate
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.views.toolbar.ToolbarContent
import com.example.util.GENDER_FEMALE
import com.example.util.GENDER_MALE
import com.example.util.showCustomTabsBrowser
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import parseAsHtmlWithoutUnderline
import setOnClickListener
import javax.inject.Inject
import javax.inject.Provider

class UserProfileMainDataFragment : BaseFragment<FragmentUserProfileMainDataBinding>(),
    UserProfileMainDataContract.View, ToolbarFragment {

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
            btnEdit.setOnClickListener(presenter::onEditClick)
        }
    }

    override fun setUserData(user: UserDetail, state: String) {
        mBinding.apply {
            tvBirthday.text = user.birthday?.value?.formatToDefaultDate()
            tvGender.text = setGender(user)
            tvRegion.text = user.address?.region
            tvCity.text = user.address?.city

            tvAdditional.text = user.notes?.value



            tvFiles.apply {
                var filesText = ""
                user.binds?.recommendationFile?.forEach { file ->
                    filesText += "<a href='${file.uri}'>${file.name}</a><br><br>"
                }
                text = filesText.parseAsHtmlWithoutUnderline()

            }
        }
        BetterLinkMovementMethod.linkifyHtml(mBinding.tvFiles)
            .setOnLinkClickListener { _, url ->
                showCustomTabsBrowser(requireContext(), url)
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
    override fun actionIconContainer(view: ViewGroup) {}
    override fun scrollValue(scroll: Int) {}
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {}
}
