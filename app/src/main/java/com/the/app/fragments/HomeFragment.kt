package com.the.app.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.the.app.MainActivity
import com.the.app.OPEN_LOCATION_NOTIFICATION_CLICK
import com.the.app.R
import com.the.app.adapters.PagerAdapter
import com.the.app.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val v = inflater.inflate(R.layout.fragment_home, container, false)
        binding = FragmentHomeBinding.bind(v)
        return v

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sectionsPagerAdapter = PagerAdapter(requireContext(), childFragmentManager)
        binding.viewPager.adapter = sectionsPagerAdapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)

        if ((requireActivity() as MainActivity).intent.action ==
            OPEN_LOCATION_NOTIFICATION_CLICK
        )
            openServiceFragment()
    }
    private fun openServiceFragment() {
        binding.tabLayout.getTabAt(2)?.select()
    }


}