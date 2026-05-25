package edu.guigu.accountbook.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import edu.guigu.accountbook.ui.fragment.BillsFragment
import edu.guigu.accountbook.ui.fragment.StatisticsFragment

class MainPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> BillsFragment()
            1 -> StatisticsFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}
