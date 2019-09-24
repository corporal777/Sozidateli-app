package com.example.ui.status

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isInvisible
import androidx.core.widget.NestedScrollView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.user.User
import com.example.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_status.*
import setUserStatus
import javax.inject.Inject
import javax.inject.Provider

class StatusFragment : BaseFragment(), StatusContract.View {

    @InjectPresenter
    lateinit var presenter: StatusPresenter

    @Inject
    lateinit var presenterProvider: Provider<StatusPresenter>

    @ProvidePresenter
    fun providePresenter(): StatusPresenter = presenterProvider.get().apply {
        status = arguments!!.getSerializable(ARG_STATUS) as User.Status
    }

    private val completeIcon by lazy {
        ContextCompat.getDrawable(requireContext(), R.drawable.ic_status_complete)
    }

    private val uncompleteIcon by lazy {
        ContextCompat.getDrawable(requireContext(), R.drawable.ic_status_uncomplete)
    }

    private val scrollListener = NestedScrollView.OnScrollChangeListener { _, _, _, _, _ ->
        statusScrollListener?.run {
            invoke(scrollContainer.scrollY)
        }
    }

    var statusScrollListener: ((scrollY: Int) -> Unit)? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scrollContainer.setOnScrollChangeListener(scrollListener)
    }

    override fun setStatus(status: User.Status, isCurrentStatus: Boolean, phone: String?, isProfileComplete: Boolean) {
        tvStatus.apply {
            setUserStatus(status)
        }

        tvYourStatus.isInvisible = !isCurrentStatus

        val isSecurityComplete: Boolean
        val isSupportComplete: Boolean
        val isInvitesComplete: Boolean
        val isPhoneComplete: Boolean
        when (status) {
            User.Status.LOW_PROTECTION -> {
                isSecurityComplete = false
                isSupportComplete = false
                isInvitesComplete = false
                isPhoneComplete = false
            }
            User.Status.MID_PROTECTION -> {
                isSecurityComplete = true
                isSupportComplete = true
                isInvitesComplete = false
                isPhoneComplete = false
            }
            User.Status.MAX_PROTECTION -> {
                isSecurityComplete = true
                isSupportComplete = true
                isInvitesComplete = true
                isPhoneComplete = false
            }
        }

        setCompleteIcon(tvSecurity, isSecurityComplete)
        setCompleteIcon(tvSupport, isSupportComplete)
        setCompleteIcon(tvInvites, isInvitesComplete)
        setCompleteIcon(tvPhone, isPhoneComplete)
        setCompleteIcon(tvProfile, isProfileComplete)
        setCompleteIcon(tvPhone, phone != null)
    }

    private fun setCompleteIcon(textView: TextView, isComplete: Boolean) {
        textView.setCompoundDrawablesWithIntrinsicBounds(if (isComplete) completeIcon else uncompleteIcon, null, null, null)
    }

    fun setContentAlpha(alpha: Float) {
        tvYourStatus.alpha = alpha
        llContent.alpha = alpha
    }

    fun getStatusViewLocation(): Int {
        return tvStatus.let {
            val location = IntArray(2).apply { it.getLocationOnScreen(this) }
            location[1] + it.height
        }
    }

    fun scrollStatusTo(y: Int) {
        scrollContainer.apply {
            setOnScrollChangeListener(null as NestedScrollView.OnScrollChangeListener?)
            scrollTo(0, y)
            setOnScrollChangeListener(scrollListener)
        }
    }

    override fun layout() = R.layout.fragment_status

    companion object {
        private const val ARG_STATUS = "status"
        fun initWithStatus(status: User.Status): StatusFragment {
            return StatusFragment().apply {
                arguments = bundleOf(ARG_STATUS to status)
            }
        }
    }
}
