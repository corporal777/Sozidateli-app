package com.example.ui.views.accountView

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.arellomobile.mvp.MvpDelegate
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.App
import com.example.R
import kotlinx.android.synthetic.main.image_with_badge.view.*
import javax.inject.Inject
import javax.inject.Provider

class AccountView : FrameLayout, AccountViewContract.View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    companion object {
        private const val ACCOUNT_TAG_VIEW = "account_view_tag"
    }

    private val mvpDelegate by lazy { MvpDelegate<AccountView>(this) }

    @InjectPresenter(type = PresenterType.WEAK, tag = ACCOUNT_TAG_VIEW)
    lateinit var presenter: AccountViewPresenter

    @Inject
    lateinit var presenterProvider: Provider<AccountViewPresenter>

    @ProvidePresenter(type = PresenterType.WEAK, tag = ACCOUNT_TAG_VIEW)
    fun providePresenter(): AccountViewPresenter = presenterProvider.get()

    private var view: View = LayoutInflater.from(context).inflate(R.layout.image_with_badge, this, true).apply {
        ivImage.setImageResource(R.drawable.avatar_placeholder)
    }

    override fun setCount(count: String) {
        view.tvBadge.text = count
    }

    override fun showCounter(show: Boolean) {
        view.tvBadge.visibility = if (show) View.VISIBLE else View.GONE
    }

    init {
        (context.applicationContext as App).appComponent.inject(this)
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