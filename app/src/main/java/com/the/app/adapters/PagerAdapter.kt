package com.the.app.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.the.app.R
import com.the.app.fragments.FragmentLoad
import com.the.app.fragments.FragmentSave
import com.the.app.fragments.FragmentServise

private val TITLES = arrayOf(
    R.string.app_text_1,
    R.string.app_text_2,
    R.string.app_text_3
)
class PagerAdapter (private val context: Context, fm: FragmentManager)
    : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> FragmentLoad()
            1 -> FragmentSave()
            else -> FragmentServise()
        }
    }

    override fun getPageTitle(position: Int): CharSequence {
        return context.resources.getString(TITLES[position])
    }

    override fun getCount() = 3
}