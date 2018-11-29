package com.example.ui.main

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.ui.base.BaseFragmentActivity
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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setSupportActionBar(toolbar)
        val navController = findNavController()
        navController.addOnNavigatedListener(navigatedListener)
    }

    override fun onSupportNavigateUp() = findNavController().navigateUp()

    override fun onBackPressed() {
        onSupportNavigateUp()
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

    override fun onDestroy() {
        findNavController().removeOnNavigatedListener(navigatedListener)
        super.onDestroy()
    }



    override fun layout() = R.layout.activity_main
}
