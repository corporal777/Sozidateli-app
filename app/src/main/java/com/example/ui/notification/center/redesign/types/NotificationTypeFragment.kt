package com.example.ui.notification.center.redesign.types

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.databinding.LayoutListBinding
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragmentNew
import com.example.ui.notification.center.redesign.NotificationType
import com.example.ui.notification.center.redesign.NotificationsListPresenter
import com.example.ui.views.toolbar.ToolbarCircleButton
import com.example.ui.views.toolbar.ToolbarContent
import com.example.ui.views.toolbar.ToolbarIconView
import onScrolled
import javax.inject.Inject
import javax.inject.Provider

class NotificationTypeFragment : BaseFragmentNew<LayoutListBinding>(), NotificationTypeContract.View, ToolbarFragment {

    private lateinit var toolbarContent: ToolbarContent

    @InjectPresenter
    lateinit var presenter: NotificationTypePresenter

    @Inject
    lateinit var presenterProvider: Provider<NotificationTypePresenter>

    @ProvidePresenter
    fun providePresenter(): NotificationTypePresenter = presenterProvider.get().apply {
        NotificationTypeFragmentArgs.fromBundle(requireArguments()).let {
            this.notificationType = it.notificationType
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun setToolbarTitle(titleRes: Int) {
        toolbarContent.setToolbarTitle(getString(titleRes))
    }

    override fun layout(): Int = R.layout.layout_list
    override val title: CharSequence by lazy { "" }
    override fun actionIconContainer(view: ViewGroup) {
        view.apply {
            addView(ToolbarCircleButton(context).apply {
                text = context.getString(R.string.read_all)
                setOnClickListener {  }
            })
        }
    }
    override fun scrollValue(scroll: (value: Int) -> Unit) {
        mBinding.recyclerView.apply {
            scroll.invoke(computeVerticalScrollOffset())
            onScrolled { _, _ ->  scroll.invoke(computeVerticalScrollOffset()) }
        }
    }
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {
        this.toolbarContent = toolbarContent
    }
}