package com.example.adapters

class TabsFragmentAdapter(
        private val fragments: List<Pair<androidx.fragment.app.Fragment, String>>,
        fragmentManager: androidx.fragment.app.FragmentManager
) : androidx.fragment.app.FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int) = fragments[position].first

    override fun getPageTitle(position: Int) = fragments[position].second

    override fun getCount() = fragments.size
}