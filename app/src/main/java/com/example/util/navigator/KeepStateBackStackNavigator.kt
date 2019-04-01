package com.example.util.navigator


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigator
import com.example.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Navigator.Name("keep_state_fragment_back_stack")
class KeepStateBackStackNavigator(
        private val context: Context,
        private val manager: FragmentManager, // Should pass childFragmentManager.
        private val containerId: Int,
        private val arrayMainFragments: ArrayList<Int>,
        private val bottomNavigationView: BottomNavigationView
) : FragmentNavigator(context, manager, containerId) {

    private val backStack = Stack<Int>()

    private val childBackStack = HashMap<Int, Stack<Int>>().apply {
        arrayMainFragments.forEach {
            put(it, Stack())
        }
    }

    private var currentFragmentId = -1

    private var lastMainFragment = -1


    override fun popBackStack(): Boolean {
        return customPopBackStack()
    }

    override fun navigate(
            destination: Destination,
            args: Bundle?,
            navOptions: NavOptions?,
            navigatorExtras: Navigator.Extras?
    ): NavDestination? {

        val destinationId = getIdForNavigate(destination.id)

        if (currentFragmentId == destinationId) {
            return null
        }

        val tag = destinationId.toString()
        val transaction = manager.beginTransaction()

        val currentFragment = manager.primaryNavigationFragment
        if (currentFragment != null) {
            transaction.detach(currentFragment)
        }

        var fragment = manager.findFragmentByTag(tag)
        if (fragment == null) {
            val className = destination.className
            fragment = instantiateFragment(context, manager, className, args)
            transaction.add(containerId, fragment, tag)
        } else {
            transaction.attach(fragment)
        }

        if (!backStack.isEmpty()) {
            transaction.addToBackStack(tag)
        }

        commitTransactionWithAnimation(transaction, fragment, navOptions)

        saveIdNavigate(destinationId)


        return destination
    }

    private fun saveIdNavigate(id: Int) {
        currentFragmentId = id

        if (arrayMainFragments.contains(currentFragmentId)) {
            lastMainFragment = currentFragmentId
            selectBottomNavItem(lastMainFragment)
            backStack.push(id)
        } else {
            childBackStack[lastMainFragment]?.push(currentFragmentId)
        }
    }

    private fun getIdForNavigate(id: Int): Int {
        if (arrayMainFragments.contains(currentFragmentId)) {
            if (childBackStack[id].isNullOrEmpty()) {
                return id
            } else {
                return childBackStack[id]?.peek()!!
            }
        } else {
            return id
        }
    }

    private fun customPopBackStack(): Boolean {
        if (arrayMainFragments.contains(currentFragmentId)) {
            if (backStack.size == 1) return false
            backStack.pop()
            val fragmentForNavigate = backStack.peek()
            if (childBackStack[fragmentForNavigate].isNullOrEmpty()) {
                return navigateById(fragmentForNavigate, fragmentForNavigate)
            } else {
                return navigateById(childBackStack[fragmentForNavigate]?.peek(), fragmentForNavigate)
            }
        } else {
            childBackStack[lastMainFragment]?.pop()
            val fragmentForNavigate = if (childBackStack[lastMainFragment].isNullOrEmpty()) backStack.peek() else childBackStack[lastMainFragment]?.peek()
            return navigateById(fragmentForNavigate, lastMainFragment)
        }
    }

    private fun navigateById(id: Int?, mainFragment: Int? = null): Boolean {
        if (id == null) return false

        val tag = id.toString()
        val transaction = manager.beginTransaction()

        val currentFragment = manager.primaryNavigationFragment

        if (currentFragment != null) {
            transaction.detach(currentFragment)
        }

        val fragment = manager.findFragmentByTag(tag)

        if (fragment != null) {
            transaction.attach(fragment)
        } else {
            return false
        }


        commitTransactionWithAnimation(transaction, fragment, getNavOptions())


        currentFragmentId = id
        if (arrayMainFragments.contains(currentFragmentId)) {
            lastMainFragment = currentFragmentId
        }
        mainFragment?.let {
            lastMainFragment = it
            selectBottomNavItem(it)
        }

        return true
    }

    private fun commitTransactionWithAnimation(transaction: FragmentTransaction, fragment: Fragment, navOptions: NavOptions?) {
        var enterAnim = navOptions?.enterAnim ?: -1
        var exitAnim = navOptions?.exitAnim ?: -1
        var popEnterAnim = navOptions?.popEnterAnim ?: -1
        var popExitAnim = navOptions?.popExitAnim ?: -1
        if (enterAnim != -1 || exitAnim != -1 || popEnterAnim != -1 || popExitAnim != -1) {
            enterAnim = if (enterAnim != -1) enterAnim else 0
            exitAnim = if (exitAnim != -1) exitAnim else 0
            popEnterAnim = if (popEnterAnim != -1) popEnterAnim else 0
            popExitAnim = if (popExitAnim != -1) popExitAnim else 0
            transaction.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim)
        }

        transaction.setPrimaryNavigationFragment(fragment)
        transaction.setReorderingAllowed(true)
        transaction.commit()
    }

    override fun onSaveState(): Bundle? {
        val bundle = super.onSaveState() ?: Bundle()
        bundle.putIntArray(KEY_BACK_STACK_IDS, backStack.toIntArray())
        bundle.putSerializable(KEY_CHILD_BACK_STACK_IDS, childBackStack)
        return bundle
    }

    override fun onRestoreState(savedState: Bundle?) {
        super.onRestoreState(savedState)
        savedState?.getIntArray(KEY_BACK_STACK_IDS)?.forEach {
            backStack.push(it)
        }
        savedState?.getSerializable(KEY_CHILD_BACK_STACK_IDS)?.let {
            if (it is HashMap<*, *>) {
                it.forEach { (key, value) ->
                    if (key is Int && value is Stack<*>) {
                        childBackStack[key] = (value as Stack<Int>)
                    }
                }
            }
        }
    }

    private fun getNavOptions(): NavOptions {
        return NavOptions.Builder()
                .setEnterAnim(androidx.navigation.ui.R.anim.nav_default_enter_anim)
                .setExitAnim(androidx.navigation.ui.R.anim.nav_default_exit_anim)
                .setPopEnterAnim(androidx.navigation.ui.R.anim.nav_default_pop_enter_anim)
                .setPopExitAnim(androidx.navigation.ui.R.anim.nav_default_pop_exit_anim)
                .build()
    }

    private fun selectBottomNavItem(idNavigate: Int?) {
        if (idNavigate == null) return
        val menu = bottomNavigationView.menu
        val aboutAlias = R.id.about_event_navigation
        val about = R.id.about_event_fragment
        val id: Int

        if (idNavigate == about) {
            id = aboutAlias
        } else {
            id = idNavigate
        }

        menu.findItem(id)?.let {
            it.isChecked = true
        }
    }


    companion object {
        private const val KEY_BACK_STACK_IDS = "back_stack_ids"
        private const val KEY_CHILD_BACK_STACK_IDS = "child_back_stack_ids"
    }
}