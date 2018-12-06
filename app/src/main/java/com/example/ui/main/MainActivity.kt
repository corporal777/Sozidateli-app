package com.example.ui.main

import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
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

    private val navigatedListener = NavController.OnNavigatedListener { controller, destination ->
        destination.defaultArguments
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        val navController = findNavController()
        navController.addOnNavigatedListener(navigatedListener)
        setupActionBarWithNavController(navController)
    }

    override fun onSupportNavigateUp() = findNavController().popBackStack()

    override fun onBackPressed() {
        if (!onSupportNavigateUp()) {
            finish()
        }
    }

    private fun isCurrentNavigationAreSame(navId: Int): Boolean {
        return findNavController().currentDestination?.id == navId
    }

    private fun findNavController() = findNavController(R.id.navHostFragment)

//    @Subscribe
//    fun onEvent(event: ShowGroupEvent) {
//    }

    override fun onStart() {
        super.onStart()
//        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
//        EventBus.getDefault().unregister(this)
    }

    override fun hideToolbar() {
        supportActionBar?.hide()
        toolbarDivider.visibility = View.GONE
    }

    override fun showToolbar() {
        supportActionBar?.show()
        toolbarDivider.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        findNavController().removeOnNavigatedListener(navigatedListener)
        super.onDestroy()
    }

    override fun onBack() {
        onBackPressed()
    }

    override fun layout() = R.layout.activity_main
}
