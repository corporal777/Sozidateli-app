package com.example.ui.views.suggestFieldView.organization

import android.content.Context
import android.view.LayoutInflater
import androidx.core.view.isVisible
import com.example.App
import com.example.R
import com.example.data.models.OrganizationNew
import com.example.databinding.BottomSheetEventFormatBinding
import com.example.util.SimpleTextWatcher
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import moxy.MvpDelegate
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import com.example.extensions.onFocusChanged
import javax.inject.Inject
import javax.inject.Provider

class EventOrgBottomSheet(
    context: Context,
    private val organizations: List<OrganizationNew>?
) : BottomSheetDialog(context), EventOrgBottomSheetContract.View {

    private val mBinding = BottomSheetEventFormatBinding.inflate(LayoutInflater.from(context))
    private val mvpDelegate by lazy { MvpDelegate(this) }

    private var onOrganizationSelected: (org: OrganizationNew?) -> Unit = {}

    @InjectPresenter
    lateinit var presenter: EventOrgBottomSheetPresenter

    @Inject
    lateinit var presenterProvider: Provider<EventOrgBottomSheetPresenter>

    @ProvidePresenter
    fun providePresenter(): EventOrgBottomSheetPresenter = presenterProvider.get().apply {
        listOrganizations.addAll(organizations?: emptyList())
    }

    private val simpleTextWatcher = SimpleTextWatcher().setAfterTextChangeRunnable {
        presenter.onOrganizationChange(it.toString())
        mBinding.apply {
            btnClear.isVisible = !it.toString().isNullOrEmpty()
        }
    }

    private val groupAdapter by lazy { GroupAdapter<GroupieViewHolder>() }

    init {
        setContentView(mBinding.root)
        (context.applicationContext as App).appComponent.inject(this)
        this.behavior.apply {
            skipCollapsed = true
            state = BottomSheetBehavior.STATE_EXPANDED
        }

        mBinding.apply {
            tvBottomSheetLabel.text = context.getString(R.string.profile_work_organization)
            ivBack.setOnClickListener { dismiss() }
            btnClear.apply {
                btnClear.isVisible = !etSearch.text.isNullOrEmpty()
                setOnClickListener { etSearch.text = null }
            }
            etSearch.apply {
                addTextChangedListener(simpleTextWatcher)
                onFocusChanged { hasFocus ->
                    clSearch.setBackgroundResource(
                        if (hasFocus) R.drawable.background_search_field_rounded_focused
                        else R.drawable.background_search_field_rounded_normal
                    )
                }
            }
            listContent.adapter = groupAdapter

        }
    }

    override fun setOrganizations(list: List<OrganizationNew>) {
        groupAdapter.apply {
            update(list.map {
                EventOrgItem(
                    it.id,
                    it.legalInformation?.name?.short
                ) { org ->
                    presenter.onOrganizationSelected(org)
                }
            })
        }
    }

    override fun performOnItemSelected(item: OrganizationNew?) {
        setTextWithoutSearch(item?.legalInformation?.name?.short)
        onOrganizationSelected.invoke(item)
        dismiss()
    }


    private fun setTextWithoutSearch(text: String?) {
        mBinding.etSearch.apply {
            removeTextChangedListener(simpleTextWatcher)
            setText(text)
            addTextChangedListener(simpleTextWatcher)
        }
    }

    fun setOrganizationSelectedCallback(block: (id: OrganizationNew?) -> Unit): EventOrgBottomSheet {
        onOrganizationSelected = block
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