package com.example.ui.views.suggestFieldView.format

import android.content.Context
import android.view.LayoutInflater
import androidx.core.view.isVisible
import com.example.App
import com.example.R
import com.example.data.models.NewEventFormat
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

class EventFormatBottomSheet(
    context: Context,
    private val formats: List<NewEventFormat>?
) : BottomSheetDialog(context), EventFormatBottomSheetContract.View {

    private val mBinding = BottomSheetEventFormatBinding.inflate(LayoutInflater.from(context))
    private val mvpDelegate by lazy { MvpDelegate(this) }

    private var onFormatSelected: (format: NewEventFormat?) -> Unit = {}

    @InjectPresenter
    lateinit var presenter: EventFormatBottomSheetPresenter

    @Inject
    lateinit var presenterProvider: Provider<EventFormatBottomSheetPresenter>

    @ProvidePresenter
    fun providePresenter(): EventFormatBottomSheetPresenter = presenterProvider.get().apply {
        listFormats.addAll(formats?: emptyList())
    }

    private val simpleTextWatcher = SimpleTextWatcher().setAfterTextChangeRunnable {
        presenter.onFormatChange(it.toString())
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
            tvBottomSheetLabel.text = context.getString(R.string.search_filter_format)
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

    override fun setFormats(list: List<NewEventFormat>) {
        groupAdapter.apply {
            update(list.map {
                EventFormatItem(
                    it.id?.toLong(),
                    it.name,
                    it.hasMask
                ) { format ->
                    presenter.onFormatSelected(format)
                }
            })
        }
    }


    override fun performOnItemSelected(item: NewEventFormat?) {
        setTextWithoutSearch(item?.name)
        onFormatSelected.invoke(item)
        dismiss()
    }

    fun setTextWithoutSearch(text: String?) {
        mBinding.etSearch.apply {
            removeTextChangedListener(simpleTextWatcher)
            setText(text)
            addTextChangedListener(simpleTextWatcher)
        }
    }

    fun setFormatSelectedCallback(block: (format: NewEventFormat?) -> Unit): EventFormatBottomSheet {
        onFormatSelected = block
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
