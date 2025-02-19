package com.example.ui.views.filters.chat

import android.content.Context
import android.view.LayoutInflater
import com.example.App
import com.example.app.databinding.BottomSheetChatFiltersBinding
import com.example.app.databinding.BottomSheetUserFiltersBinding
import com.example.data.models.InterestNew
import com.example.data.models.NewEventFormat
import com.example.data.models.SearchFilter
import com.example.extensions.initDropDownView
import com.example.extensions.onTextChanged
import com.example.ui.views.filters.user.UserFiltersBottomSheetContract
import com.example.ui.views.filters.user.UserFiltersBottomSheetDialog
import com.example.ui.views.filters.user.UserFiltersBottomSheetPresenter
import com.example.ui.views.suggestFieldView.region.SearchRegionBottomSheet
import com.example.ui.views.suggestFieldView.town.SearchTownBottomSheet
import com.example.util.initInput
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import moxy.MvpDelegate
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class ChatFiltersBottomSheetDialog(
    val requireContext: Context,
    filter: SearchFilter.UserNew
) : BottomSheetDialog(requireContext), ChatFiltersBottomSheetContract.View {

    private val mBinding = BottomSheetChatFiltersBinding.inflate(LayoutInflater.from(requireContext))
    private val mvpDelegate by lazy { MvpDelegate(this) }

    @InjectPresenter
    lateinit var presenter: ChatFiltersBottomSheetPresenter

    @Inject
    lateinit var presenterProvider: Provider<ChatFiltersBottomSheetPresenter>

    @ProvidePresenter
    fun providePresenter(): ChatFiltersBottomSheetPresenter = presenterProvider.get()

    private var onFiltersApply: (filter: SearchFilter.UserNew) -> Unit = {}

    private var userFilter = filter

    init {
        setContentView(mBinding.root)
        (context.applicationContext as App).appComponent.inject(this)
        this.behavior.apply {
            skipCollapsed = true
            state = BottomSheetBehavior.STATE_EXPANDED
        }

        mBinding.apply {
            btnClose.setOnClickListener { dismiss() }
            btnClear.setOnClickListener { clearFiltersView() }
            btnApply.setOnClickListener {
                onFiltersApply.invoke(userFilter)
                dismiss()
            }
        }
    }

    override fun initDaDataAddress() {
        mBinding.etAddress.apply {
            setTextWithoutSearch(userFilter.address)
            onTextChanged {
                userFilter.address = it.toString()
                if (userFilter.address.isNullOrBlank()) userFilter.setAddressFilter(null)
            }
            onDataSelectedListener = { userFilter.setAddressFilter(it) }
        }
    }


    override fun initInterests(interests: Map<InterestNew, List<InterestNew>>) {
        val themes = interests.keys
        val selectedTheme = presenter.findInterest(userFilter.theme, themes)
        initDropDownView(
            mBinding.tvTheme,
            themes,
            selectedTheme?.name,
            null,
            transformKey = { it.name ?: "" },
            findValue = { it?.id },
            onVariantChange = { id ->
                userFilter.theme = id
                userFilter.spec = null

                val specs = presenter.findInterest(id, themes)?.let { interests[it] }
                initSpecializations(specs, userFilter.spec)
            }
        )

        val specs = selectedTheme?.let { interests[it] }
        initSpecializations(specs, userFilter.spec)
    }

    private fun initSpecializations(interests: List<InterestNew>?, spec: Int?) {
        if (interests == null) {
            mBinding.tvSpec.apply {
                isEnabled = false
                text = null
            }
            mBinding.tilSpec.isEnabled = false
        } else {
            mBinding.tvSpec.isEnabled = true
            mBinding.tilSpec.isEnabled = true
            val selectedTheme = presenter.findInterest(spec, interests)
            initDropDownView(
                mBinding.tvSpec,
                interests,
                selectedTheme?.name,
                null,
                transformKey = { it.name ?: "" },
                findValue = { it?.id },
                onVariantChange = {
                    userFilter.spec = it
                })
        }
    }

    override fun initFormats(formats: List<NewEventFormat>) {}

    private fun clearFiltersView() {
        mBinding.apply {
            etAddress.text = null
            tvTheme.text = null
            tvSpec.text = null
        }
    }

    fun setFiltersSelected(block: (filters: SearchFilter.UserNew) -> Unit): ChatFiltersBottomSheetDialog {
        onFiltersApply = block
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