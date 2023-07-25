package com.example.adapters

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntDef
import androidx.fragment.app.*
import androidx.lifecycle.Lifecycle
import androidx.viewpager.widget.PagerAdapter
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

abstract class PagerStateAdapter : PagerAdapter {
    private val TAG = "FragmentStatePagerAdapt"
    private val DEBUG = false

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(VISIBLE_HINT, RESUME_ONLY_CURRENT_FRAGMENT)
    private annotation class Behavior()


    private var mFragmentManager: FragmentManager? = null
    private var mBehavior = 0
    private var mCurTransaction: FragmentTransaction? = null

    private val mSavedState = ArrayList<Fragment.SavedState?>()
    private val mFragments = ArrayList<Fragment?>()
    private var mCurrentPrimaryItem: Fragment? = null
    private var mExecutingFinishUpdate = false


    constructor(fm: FragmentManager) {
        mFragmentManager = fm
        mBehavior = RESUME_ONLY_CURRENT_FRAGMENT
    }
    constructor(fm: FragmentManager, @Behavior behavior: Int) {
        mFragmentManager = fm
        mBehavior = behavior
    }


    abstract fun getItem(position: Int): Fragment

    override fun startUpdate(container: ViewGroup) {
        if (container.id == View.NO_ID) {
            throw IllegalStateException(
                "ViewPager with adapter " + this
                        + " requires a view id"
            )
        }
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        if (mFragments.size > position) {
            val f = mFragments[position]
            if (f != null) {
                return f
            }
        }
        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager!!.beginTransaction()
        }
        val fragment = getItem(position)
        if (DEBUG) Log.v(TAG, "Adding item #$position: f=$fragment")
        if (mSavedState.size > position) {
            val fss = mSavedState[position]
            if (fss != null) fragment.setInitialSavedState(fss)

        }
        while (mFragments.size <= position) mFragments.add(null)

        fragment.setMenuVisibility(false)
        if (mBehavior == VISIBLE_HINT) fragment.userVisibleHint = false

        mFragments[position] = fragment
        mCurTransaction!!.add(container.id, fragment)
        if (mBehavior == RESUME_ONLY_CURRENT_FRAGMENT) {
            mCurTransaction!!.setMaxLifecycle(fragment, Lifecycle.State.STARTED)
        }
        return fragment
    }


    @SuppressLint("LogNotTimber")
    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        val fragment = obj as Fragment
        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager!!.beginTransaction()
        }
        if (DEBUG) Log.v(
            TAG,
            ("Removing item #" + position + ": f=" + fragment + " v=" + fragment.view)
        )
        while (mSavedState.size <= position) mSavedState.add(null)

        mSavedState[position] =
            if (fragment.isAdded) mFragmentManager!!.saveFragmentInstanceState(fragment) else null
        mFragments.set(position, null)
        mCurTransaction!!.remove(fragment)
        if ((fragment == mCurrentPrimaryItem)) {
            mCurrentPrimaryItem = null
        }
    }

    override fun setPrimaryItem(container: ViewGroup, position: Int, obj: Any) {
        try {
            val fragment = obj as Fragment
            if (fragment !== mCurrentPrimaryItem) {
                if (mCurrentPrimaryItem != null) {
                    mCurrentPrimaryItem!!.setMenuVisibility(false)
                    if (mBehavior == RESUME_ONLY_CURRENT_FRAGMENT) {
                        if (mCurTransaction == null) {
                            mCurTransaction = mFragmentManager!!.beginTransaction()
                        }
                        mCurTransaction!!.setMaxLifecycle(
                            mCurrentPrimaryItem!!,
                            Lifecycle.State.STARTED
                        )
                    } else {
                        mCurrentPrimaryItem!!.userVisibleHint = false
                    }
                }
                fragment.setMenuVisibility(true)
                if (mBehavior == RESUME_ONLY_CURRENT_FRAGMENT) {
                    if (mCurTransaction == null) {
                        mCurTransaction = mFragmentManager!!.beginTransaction()
                    }
                    mCurTransaction!!.setMaxLifecycle(fragment, Lifecycle.State.RESUMED)
                } else {
                    fragment.userVisibleHint = true
                }
                mCurrentPrimaryItem = fragment
            }
        }catch (e : Exception){
            e.printStackTrace()
        }

    }

    override fun finishUpdate(container: ViewGroup) {
        try {
            if (mCurTransaction != null) {
                if (!mExecutingFinishUpdate) {
                    try {
                        mExecutingFinishUpdate = true
                        mCurTransaction!!.commitNowAllowingStateLoss()
                    } finally {
                        mExecutingFinishUpdate = false
                    }
                }
                mCurTransaction = null
            }
        }catch (e : Exception){
            e.printStackTrace()
        }

    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return (obj as Fragment).view === view
    }

    override fun saveState(): Parcelable? {
        return try {
            var state: Bundle? = null

            if (mSavedState.size > 0) {
                state = Bundle()
                val fss = arrayOfNulls<Fragment.SavedState>(mSavedState.size)
                mSavedState.toArray(fss)
                state.putParcelableArray("states", fss)
            }
            for (i in mFragments.indices) {
                val f: Fragment? = mFragments.get(i)
                if (f != null && f.isAdded) {
                    if (state == null) {
                        state = Bundle()
                    }
                    val key = "f$i"
                    mFragmentManager!!.putFragment(state, key, f)
                }
            }
            state
        }catch (e : Exception){
            null
        }
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {
        try {
            if (state != null) {
                val bundle = state as Bundle
                bundle.classLoader = loader
                val fss = bundle.getParcelableArray("states")
                mSavedState.clear()
                mFragments.clear()
                if (fss != null) {
                    for (i in fss.indices) {
                        mSavedState.add(fss[i] as Fragment.SavedState)
                    }
                }
                val keys: Iterable<String> = bundle.keySet()
                for (key: String in keys) {
                    if (key.startsWith("f")) {
                        val index = key.substring(1).toInt()
                        val f = mFragmentManager!!.getFragment(bundle, key)
                        if (f != null) {
                            while (mFragments.size <= index) {
                                mFragments.add(null)
                            }
                            f.setMenuVisibility(false)
                            mFragments[index] = f
                        } else {
                            Log.w(TAG, "Bad fragment at key $key")
                        }
                    }
                }
            }
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

    companion object {
        internal const val VISIBLE_HINT = 0
        internal const val RESUME_ONLY_CURRENT_FRAGMENT = 1
    }

}