package com.example.ui.views.suggestFieldView.address

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import com.example.App
import com.example.R
import com.example.adapters.NoFilterArrayAdapter
import com.example.data.models.NewUserAddress
import com.example.util.SimpleTextWatcher
import moxy.MvpDelegate
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class DaDataAutoCompleteTextView : AppCompatAutoCompleteTextView,
    DaDataAutoCompleteTextViewContract.View {


    private val mvpDelegate by lazy { MvpDelegate(this) }

    @InjectPresenter
    lateinit var presenter: DaDataAutoCompleteTextViewPresenter

    @Inject
    lateinit var presenterProvider: Provider<DaDataAutoCompleteTextViewPresenter>

    @ProvidePresenter
    fun providePresenter(): DaDataAutoCompleteTextViewPresenter = presenterProvider.get()

    var onDataSelectedListener: OnDataSelectedListener? = null

    private val adapter = NoFilterArrayAdapter<String>(context, R.layout.item_town, android.R.id.text1)
            .apply {
                setOnItemClickListener { _, _, position, _ ->
                    presenter.onItemSelected(position)
                }
            }

    private val simpleTextWatcher = SimpleTextWatcher().setAfterTextChangeRunnable {
        presenter.onQueryChange(it.toString())
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        (context.applicationContext as App).appComponent.inject(this)
        setAdapter(adapter)
        addTextChangedListener(simpleTextWatcher)
    }

    override fun setSuggested(list: List<NewUserAddress>) {
        adapter.apply {
            DaDataUtil.formatLocations(context, list)
            clear()
            addAll(list.map { it.fullValue })
            notifyDataSetChanged()
        }
    }

    fun setTextWithoutSearch(text: String?) {
        removeTextChangedListener(simpleTextWatcher)
        setText(text)
        addTextChangedListener(simpleTextWatcher)
    }

    override fun performOnItemSelected(item: NewUserAddress) {
        onDataSelectedListener?.invoke(item)
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

typealias OnDataSelectedListener = (data: NewUserAddress) -> Unit