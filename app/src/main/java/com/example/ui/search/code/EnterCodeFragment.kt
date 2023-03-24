package com.example.ui.search.code

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.databinding.FragmentEnterCodeBinding
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragmentNew
import com.example.ui.views.toolbar.ToolbarContent
import onTextChanged
import javax.inject.Inject
import javax.inject.Provider

class EnterCodeFragment : BaseFragmentNew<FragmentEnterCodeBinding>(), EnterCodeContract.View,
    ToolbarFragment {

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
                setOnKeyListener { _, keyCode, _ ->
                    when (keyCode) {
                        EditorInfo.IME_ACTION_DONE -> {
                            presenter.onSearchClick(etCode.text.toString())
                            true
                        }
                        else -> false
                    }
                }
                requestFocus()

                onTextChanged {
                    btnSearch.isEnabled = !it.isNullOrEmpty()
                }
            }
            btnSearch.apply {
                isEnabled = !etCode.text.isNullOrEmpty()
                setOnClickListener { presenter.onSearchClick(etCode.text.toString()) }
            }
        }
    }

    override fun showEvent(eventId: String) {
        findNavController().navigate(
            EnterCodeFragmentDirections.enterEventCodeFragmentToAboutEventFragment(
                eventId
            )
        )
    }

    override fun showEventNotFoundError() {
        showToast(R.string.qr_scan_not_found_event)
    }

    override fun layout() = R.layout.fragment_enter_code
    override val title: CharSequence by lazy { getString(R.string.code_input_label) }
    override fun actionIconContainer(view: ViewGroup) {}
    override fun scrollValue(scroll: (value: Int) -> Unit) { scroll.invoke(0) }
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {}
}
