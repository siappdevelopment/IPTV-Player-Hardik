package com.iptvplayer.xtreamiptv.myiptvpro.Main.Adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.iptvplayer.xtreamiptv.myiptvpro.Main.fragment.LeftFragment
import com.iptvplayer.xtreamiptv.myiptvpro.Main.fragment.MainFragment

class HomePagerAdapter(
    fragmentActivity: FragmentActivity
) : FragmentStateAdapter(fragmentActivity) {

    private var numMainPages = 1


    fun setNumMainPages(count: Int) {
        if (numMainPages != count) {
            numMainPages = count
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = numMainPages + 1

    override fun createFragment(position: Int): Fragment {
        return when (position) {

            0 -> {

                LeftFragment()

            }

            else -> MainFragment.newInstance(position - 1)
        }
    }
}
