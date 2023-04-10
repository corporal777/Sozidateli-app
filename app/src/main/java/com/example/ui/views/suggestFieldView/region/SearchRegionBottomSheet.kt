package com.example.ui.views.suggestFieldView.region

import android.content.Context
import android.view.LayoutInflater
import androidx.core.view.isVisible
import com.arellomobile.mvp.MvpDelegate
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.App
import com.example.R
import com.example.data.models.SearchRegion
import com.example.databinding.BottomSheetEventFormatBinding
import com.example.util.SimpleTextWatcher
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import onFocusChanged
import javax.inject.Inject
import javax.inject.Provider

class SearchRegionBottomSheet (
    context: Context,
) : BottomSheetDialog(context), SearchRegionBottomSheetContract.View {

    private val mBinding = BottomSheetEventFormatBinding.inflate(LayoutInflater.from(context))
    private val mvpDelegate by lazy { MvpDelegate<SearchRegionBottomSheet>(this) }

    private var onRegionSelected: (region: SearchRegion?) -> Unit = {}

    @InjectPresenter(type = PresenterType.WEAK, tag = REGIONS_TAG_VIEW)
    lateinit var presenter: SearchRegionBottomSheetPresenter

    @Inject
    lateinit var presenterProvider: Provider<SearchRegionBottomSheetPresenter>

    @ProvidePresenter(type = PresenterType.WEAK, tag = REGIONS_TAG_VIEW)
    fun providePresenter(): SearchRegionBottomSheetPresenter = presenterProvider.get().apply {

    }

    private val simpleTextWatcher = SimpleTextWatcher().setAfterTextChangeRunnable {
        presenter.onRegionChange(it.toString())
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
            tvBottomSheetLabel.text = context.getString(R.string.search_filter_region)
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

    override fun setRegions(list: List<SearchRegion>) {
        groupAdapter.apply {
            update(list.map {
                SearchItem(
                    it.id,
                    it.name,
                ) { region ->
                    presenter.onRegionSelected(region)
                }
            })
        }
    }

    override fun performOnItemSelected(item: SearchRegion?) {
        setTextWithoutSearch(item?.name)
        onRegionSelected.invoke(item)
        dismiss()
    }


    fun setTextWithoutSearch(text: String?) {
        mBinding.etSearch.apply {
            removeTextChangedListener(simpleTextWatcher)
            setText(text)
            addTextChangedListener(simpleTextWatcher)
        }
    }

    fun setRegionSelectedCallback(block: (region: SearchRegion?) -> Unit): SearchRegionBottomSheet {
        onRegionSelected = block
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

    companion object {
        private const val REGIONS_TAG_VIEW = "regions_tag"

        private const val PLACEHOLDER = R.drawable.avatar_placeholder
    }
}