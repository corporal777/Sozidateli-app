package com.example.ui.main

import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.ui.base.BaseFragmentActivity
import com.example.util.ARG_CUSTOM_LABEL
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject
import javax.inject.Provider

class MainActivity : BaseFragmentActivity(), MainContract.View {

    @InjectPresenter
    lateinit var presenter: MainPresenter

    @Inject
    lateinit var presenterProvider: Provider<MainPresenter>

    @ProvidePresenter
    fun providePresenter(): MainPresenter = presenterProvider.get()

    private val startDestinations = arrayOf(R.id.events_tabs_fragment)

    private val navigatedListener = NavController.OnDestinationChangedListener { controller, destination, arguments ->
        supportActionBar?.title = destination.label ?: arguments?.getString(ARG_CUSTOM_LABEL)

        presenter.apply {
            if (startDestinations.contains(destination.id)) onOpenStartDestination()
            else onOpenNotStartDestination()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        val navController = findNavController()
        navController.addOnDestinationChangedListener(navigatedListener)
    }

    private fun findNavController() = findNavController(R.id.navHostFragment)

    override fun hideToolbar() {
        supportActionBar?.hide()
        toolbarDivider.visibility = View.GONE
    }

    override fun onBackPressed() {
        if (!onSupportNavigateUp()) finish()
    }

    override fun navigateUp() {
        onSupportNavigateUp()
    }

    override fun onSupportNavigateUp() = findNavController().navigateUp()

    override fun showToolbar() {
        supportActionBar?.show()
        toolbarDivider.visibility = View.VISIBLE
    }

    override fun showBackButton(show: Boolean) {
        supportActionBar?.setDisplayHomeAsUpEnabled(show)
    }

    override fun onDestroy() {
        findNavController().removeOnDestinationChangedListener(navigatedListener)
        super.onDestroy()
    }

    override fun layout() = R.layout.activity_main
}
