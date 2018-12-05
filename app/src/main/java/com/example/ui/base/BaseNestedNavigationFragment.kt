package com.example.ui.base

import androidx.navigation.Navigation
import com.example.R

abstract class BaseNestedNavigationFragment : BaseFragment() {

    protected fun findParentNavigation() = Navigation.findNavController(activity!!, R.id.navHostFragment)
}