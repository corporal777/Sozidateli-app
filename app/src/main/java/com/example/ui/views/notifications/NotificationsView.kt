package com.example.ui.views.notifications

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.example.App
import com.example.R
import kotlinx.android.synthetic.main.image_with_badge.view.*
import moxy.MvpDelegate
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class NotificationsView : FrameLayout, NotificationsViewContract.View {

    companion object {
        private const val NOTIFICATIONS_TAG_VIEW = "notifications_view_tag"
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val mvpDelegate by lazy { MvpDelegate<NotificationsView>(this) }

    @InjectPresenter(tag = NOTIFICATIONS_TAG_VIEW)
    lateinit var presenter: NotificationsViewPresenter

    @Inject
    lateinit var presenterProvider: Provider<NotificationsViewPresenter>

    @ProvidePresenter(tag = NOTIFICATIONS_TAG_VIEW)
    fun providePresenter(): NotificationsViewPresenter = presenterProvider.get()

    private var view: View = LayoutInflater.from(context)
        .inflate(R.layout.image_with_badge, this, true).apply {
            ivImage.setImageResource(R.drawable.ic_profile_notification)
        }

    init {
        (context.applicationContext as App).appComponent.inject(this)
    }

    override fun showCounter(show: Boolean) {
        view.apply {
            tvBadge.visibility = if (show) View.VISIBLE else View.GONE
            ivImage.setImageResource(
                if (show) R.drawable.ic_profile_notification
                else R.drawable.ic_notifications_none
            )
        }
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

    override fun setOnClickListener(l: OnClickListener?) {
        ivImage.setOnClickListener(l)
    }
}