package com.example.ui.search.code

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.navigation.fragment.findNavController
import com.example.app.R
import com.example.app.databinding.FragmentEnterCodeBinding
import com.example.extensions.onKeyDoneClick
import com.example.extensions.onTextChanged
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseToolbarFragment
import com.example.ui.base.BaseVBFragment
import com.example.ui.event.about.AboutEventFragmentArgs
import com.example.ui.views.toolbar.ToolbarContent
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class EnterCodeFragment : BaseToolbarFragment<FragmentEnterCodeBinding>(), EnterCodeContract.View {

    @InjectPresenter
    lateinit var presenter: EnterCodePresenter

    @Inject
    lateinit var presenterProvider: Provider<EnterCodePresenter>

    @ProvidePresenter
    fun providePresenter(): EnterCodePresenter = presenterProvider.get()



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            etCode.apply {
                onKeyDoneClick { presenter.onSearchClick(etCode.text.toString()) }
                onTextChanged { btnSearch.isEnabled = !it.isNullOrEmpty() }
            }
            btnSearch.apply {
                isEnabled = !etCode.text.isNullOrEmpty()
                setOnClickListener { presenter.onSearchClick(etCode.text.toString()) }
            }
        }
    }

    override fun showEvent(eventId: String) {
        val args = AboutEventFragmentArgs.Builder(eventId).build().toBundle()
        findNavController().navigate(R.id.about_event_fragment, args)
    }

    override fun showEventNotFoundError() {
        showToast(R.string.qr_scan_not_found_event)
    }

    override fun showCustomLoading() = mBinding.btnSearch.showProgressLoading(true)
    override fun hideCustomLoading() = mBinding.btnSearch.showProgressLoading(false)

    override fun animationType(): AnimType = AnimType.FADE
    override fun binding() = FragmentEnterCodeBinding::class.java
    override fun layout() = R.layout.fragment_enter_code
    override val title: CharSequence by lazy { getString(R.string.code_input_label) }
}
