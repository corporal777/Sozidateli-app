package com.example.ui.base

import android.content.Context
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import android.widget.Toast
import dagger.android.AndroidInjection
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

abstract class BaseActivity : MvpAppCompatActivity(), BaseContract.View {

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(layout())
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    abstract fun hideToolbar()
    abstract fun showToolbar()

    @LayoutRes
    abstract fun layout(): Int

    override fun showToast(@StringRes message: Int) = showToast(getString(message))
    override fun showToast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}