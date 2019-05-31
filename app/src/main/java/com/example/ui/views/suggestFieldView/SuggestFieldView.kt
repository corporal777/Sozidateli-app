package com.example.ui.views.suggestFieldView

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.core.content.ContextCompat
import com.arellomobile.mvp.MvpDelegate
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.App
import com.example.R
import com.example.data.models.DataDataItem
import com.example.data.models.DataDataResponse
import com.example.util.SimpleTextWatcher
import kotlinx.android.synthetic.main.image_with_badge.view.*
import javax.inject.Inject
import javax.inject.Provider

class SuggestFieldView : AppCompatAutoCompleteTextView, SuggestFieldViewContract.View {

    companion object {
        private const val CHAT_VIEW_TAG = "suggest"
    }

    private val mvpDelegate by lazy { MvpDelegate<SuggestFieldView>(this) }

    @InjectPresenter(type = PresenterType.WEAK, tag = CHAT_VIEW_TAG)
    lateinit var presenter: SuggestFieldViewPresenter

    @Inject
    lateinit var presenterProvider: Provider<SuggestFieldViewPresenter>

    @ProvidePresenter(type = PresenterType.WEAK, tag = CHAT_VIEW_TAG)
    fun providePresenter(): SuggestFieldViewPresenter = presenterProvider.get()

    private val adapterData = AutoSuggestAdapter(context, android.R.layout.simple_dropdown_item_1line)
            .apply {
                setOnItemClickListener{adapterView, view, i, l ->
                    onItemSelected(getObject(i))
                }
            }

    private val simpleTextWatcher = SimpleTextWatcher().setAfterTextChangeRunnable {
        presenter.onQueryChange(it.toString())
    }

    var onItemSelected: (data: DataDataItem) -> Unit = {}

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        (context.applicationContext as App).appComponent.inject(this)
        setAdapter(adapterData)
        addTextChangedListener(simpleTextWatcher)
    }

    override fun setSuggested(list: List<DataDataItem>) {
        adapterData.setData(list)
        adapterData.notifyDataSetChanged()
    }


    fun setTextWithoutListen(text:String){
        removeTextChangedListener(simpleTextWatcher)
        setText(text)
        addTextChangedListener(simpleTextWatcher)
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