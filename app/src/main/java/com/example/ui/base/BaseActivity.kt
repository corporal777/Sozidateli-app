package com.example.ui.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import android.widget.Toast
import dagger.android.AndroidInjection

abstract class BaseActivity : MvpAppCompatActivity(), BaseContract.View {

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(layout())
    }

    abstract fun hideToolbar()
    abstract fun showToolbar()

    @LayoutRes
    abstract fun layout(): Int

    override fun showToast(@StringRes message: Int) = showToast(getString(message))
    override fun showToast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}