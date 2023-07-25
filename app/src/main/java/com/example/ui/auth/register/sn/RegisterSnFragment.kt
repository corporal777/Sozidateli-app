package com.example.ui.auth.register.sn

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.view.View
import androidx.annotation.StyleRes
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.clearSpans
import androidx.core.text.toSpannable
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.transition.Scene
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.SnUser
import com.example.databinding.FragmentRegisterSnBinding
import com.example.ui.base.BaseFragment
import com.example.ui.snAuth.SnType
import com.example.util.ClickableSpan
import kotlinx.android.synthetic.main.fragment_register_sn.*
import kotlinx.android.synthetic.main.scene_register_sn_email.view.*
import kotlinx.android.synthetic.main.scene_register_sn_password.*
import kotlinx.android.synthetic.main.scene_register_sn_password.view.*
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import onTextChanged
import setCircleImage
import javax.inject.Inject
import javax.inject.Provider

class RegisterSnFragment : BaseFragment<FragmentRegisterSnBinding>(), RegisterSnContract.View {

    @InjectPresenter
    lateinit var presenter: RegisterSnPresenter

    @Inject
    lateinit var presenterProvider: Provider<RegisterSnPresenter>

    @ProvidePresenter
    fun providePresenter(): RegisterSnPresenter = presenterProvider.get().apply {
        snUser = RegisterSnFragmentArgs.fromBundle(requireArguments()).snUser
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ivClose.setOnClickListener { presenter.onClickClose() }

        val agreementText = SpannableString(getString(R.string.auth_agree_user_agreement)).apply {
            val linkStart = 11
            val linkEnd = length
            setSpan(ClickableSpan { presenter.onClickUserAgreement() }, linkStart, linkEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        }

        tvAgree.apply {
            text = agreementText
            movementMethod = BetterLinkMovementMethod.getInstance()
        }

        flAgree.setOnClickListener {
            cbAgree.apply {
                isChecked = !isChecked
            }
        }

        cbAgree.setOnCheckedChangeListener { _, isChecked -> presenter.onClickAgree(isChecked) }

        ibRegister.setOnClickListener { presenter.onClickContinue() }
    }

    override fun onDestroyView() {
        tvAgree.text.toSpannable().clearSpans()
        super.onDestroyView()
    }

    override fun setUserData(snType: SnType, name: String?, avatar: String?) {
        val logo: Int
        val theme: Int
        when (snType) {
            SnType.VK -> {
                logo = R.drawable.ic_vk
                theme = R.style.ViewBackgroundVk
            }
            SnType.FB -> {
                logo = R.drawable.ic_facebook
                theme = R.style.ViewBackgroundFacebook
            }
            SnType.OK -> {
                logo = R.drawable.ic_ok
                theme = R.style.ViewBackgroundOk
            }
        }

        llSnUser.background = getSnBackground(theme)
        ivSnLogo.setImageResource(logo)
        ivUserAvatar.setCircleImage(avatar, R.drawable.avatar_placeholder)
        tvUserName.text = name
    }

    private fun getSnBackground(@StyleRes style: Int): Drawable? {
        return ResourcesCompat.getDrawable(
            resources,
            R.drawable.background_solid,
            ContextThemeWrapper(requireContext(), style).theme
        )
    }

    override fun setEmail(email: String?) {
        sceneRoot.etEmail.apply {
            setText(email)
            onTextChanged { text -> text?.toString()?.let { presenter.onChangeEmailText(it) } }
        }
    }

    override fun setPassword(password: String?, passwordConfirm: String?) {
        ibRegister.setText(R.string.auth_action_register)
        Scene.getSceneForLayout(sceneRoot, R.layout.scene_register_sn_password, requireContext()).apply {
            setEnterAction {
                sceneRoot.etPassword.apply {
                    onTextChanged { text -> text?.toString()?.let { presenter.onChangePasswordText(it) } }
                    setText(password)
                    requestFocus()
                }
                sceneRoot.etPasswordConfirm.apply {
                    onTextChanged { text -> text?.toString()?.let { presenter.onChangePasswordConfirmText(it) } }
                    setText(passwordConfirm)
                }
            }

            enter()
        }

        llAgree.isVisible = true
    }

    override fun showPasswordError(show: Boolean) {
        sceneRoot.tilPassword?.error = if (show) getString(
            if (etPassword.text.isNullOrEmpty()) R.string.auth_error_no_password
            else R.string.auth_error_short_password
        ) else null
    }

    override fun showPasswordConfirmError(show: Boolean) {
        sceneRoot.tilPasswordConfirm?.error = if (show) getString(R.string.auth_error_password_do_not_match) else null
    }

    override fun showAgreementError(show: Boolean) {
        tvAgreeError.isInvisible = !show
    }

    override fun enableContinueButton(isEnable: Boolean) {
        ibRegister.apply { isEnabled = isEnable }
    }

    override fun showEmailConfirmation(email: String, snUser: SnUser?) {
        findNavController().navigate(RegisterSnFragmentDirections.snRegisterToEmailConfirm(email, null, snUser))
    }

    override fun showUserAgreement() {
        findNavController().navigate(R.id.agreement_fragment)
    }

    override fun layout() = R.layout.fragment_register_sn
}
