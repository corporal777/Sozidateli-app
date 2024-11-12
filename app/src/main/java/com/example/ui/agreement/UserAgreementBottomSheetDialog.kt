package com.example.ui.agreement

import android.content.Context
import android.view.LayoutInflater
import com.example.App
import com.example.app.R
import com.example.app.databinding.BottomSheetDialogEventAgreementBinding
import com.example.data.models.EventNew
import com.example.data.models.SearchFilter
import com.example.ui.views.CustomSpannableString
import com.example.ui.views.filters.event.EventFiltersBottomSheetDialog
import com.example.util.showCustomTabsBrowser
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.reactivex.subjects.SingleSubject
import moxy.MvpDelegate
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class UserAgreementBottomSheetDialog  (
    context: Context,
    event : EventNew
) : BottomSheetDialog(context), UserAgreementBottomSheetContract.View {

    @InjectPresenter
    lateinit var presenter: UserAgreementBottomSheetPresenter

    @Inject
    lateinit var presenterProvider: Provider<UserAgreementBottomSheetPresenter>

    @ProvidePresenter
    fun providePresenter(): UserAgreementBottomSheetPresenter = presenterProvider.get()


    private val mBinding = BottomSheetDialogEventAgreementBinding.inflate(LayoutInflater.from(context))
    private val mvpDelegate by lazy { MvpDelegate(this) }

    private var onAcceptCallback: (isAccept: Boolean, event : EventNew) -> Unit = { _, _ -> }

    init {
        setContentView(mBinding.root)

        (context.applicationContext as App).appComponent.inject(this)

        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.skipCollapsed = true
        setCancelable(false)

        mBinding.apply {
            btnClose.setOnClickListener {
                presenter.notAcceptAgreement(event)
            }
            btnApply.apply {
                showProgressLoading(false)
                isEnabled = false
                setOnClickListener {
                    presenter.acceptAgreement(event)
                }
            }
            viewAgreement.apply {
                getTextView().apply {
                    text = CustomSpannableString(context.getString(R.string.auth_agree_user_agreement)).apply {
                        setFontSpan("fonts/sf_pro_display_semibold.ttf", context, 11)
                        setClickSpanWithLength(getTextView(), 11, length){
                            showCustomTabsBrowser(context, event.userAgreement?.uri ?: "")
                        }
                    }
                }
                setOnCheckedListener {
                    btnApply.isEnabled = it
                }
            }
        }
    }

    override fun showCustomLoading(show: Boolean) {
        mBinding.btnApply.showProgressLoading(show)
    }

    override fun setAcceptAgreement(isAccept: Boolean, eventNew: EventNew) {
        onAcceptCallback.invoke(isAccept, eventNew)
        dismiss()
    }

    fun setAcceptedCallback(block: (isAccept: Boolean, eventNew: EventNew) -> Unit): UserAgreementBottomSheetDialog {
        onAcceptCallback = block
        return this
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mvpDelegate.onCreate()
        mvpDelegate.onAttach()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mvpDelegate.onSaveInstanceState()
        mvpDelegate.onDetach()
        mvpDelegate.onDestroyView()
        mvpDelegate.onDestroy()
    }
}