package com.example.ui.main

import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.ui.base.BaseFragmentActivity
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

    private val navigatedListener = NavController.OnNavigatedListener { controller, destination ->
        supportActionBar?.title = destination.label

        presenter.apply {
            if (startDestinations.contains(destination.id)) onOpenStartDestination()
            else onOpenNotStartDestination()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        val navController = findNavController()
        navController.addOnNavigatedListener(navigatedListener)
    }

    override fun onSupportNavigateUp() = findNavController().navigateUp()

    override fun onBackPressed() {
        if (!onSupportNavigateUp()) finish()
    }

    private fun findNavController() = findNavController(R.id.navHostFragment)

    override fun hideToolbar() {
        supportActionBar?.hide()
        toolbarDivider.visibility = View.GONE
    }

    override fun navigateUp() {
        findNavController().navigateUp()
    }

    override fun showToolbar() {
        supportActionBar?.show()
        toolbarDivider.visibility = View.VISIBLE
    }

    override fun showBackButton(show: Boolean) {
        supportActionBar?.setDisplayHomeAsUpEnabled(show)
    }

    override fun onDestroy() {
        findNavController().removeOnNavigatedListener(navigatedListener)
        super.onDestroy()
    }

    override fun layout() = R.layout.activity_main
}
