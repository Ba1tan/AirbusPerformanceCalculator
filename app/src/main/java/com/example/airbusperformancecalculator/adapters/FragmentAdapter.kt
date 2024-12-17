package com.example.airbusperformancecalculator.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.airbusperformancecalculator.fragments.CalcFragment
import com.example.airbusperformancecalculator.fragments.MetarFragment

class FragmentAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle)
{
    override fun createFragment(position: Int): Fragment {
        return if (position == 0)
            CalcFragment()
        else
            MetarFragment()
    }

    override fun getItemCount(): Int {
        return 2
    }

}