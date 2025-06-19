package com.example.playlistmaker.library.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentLibraryBinding
import com.example.playlistmaker.library.ui.adapter.LibraryViewPagerAdapter
import com.example.playlistmaker.util.BindingFragment
import com.google.android.material.tabs.TabLayoutMediator

class LibraryFragment : BindingFragment<FragmentLibraryBinding>() {

    private lateinit var tabLayoutMediator: TabLayoutMediator

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLibraryBinding {
        return FragmentLibraryBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPagerLibrary.adapter = LibraryViewPagerAdapter(childFragmentManager, lifecycle)

        tabLayoutMediator =
            TabLayoutMediator(binding.tabLibrary, binding.viewPagerLibrary) { tab, position ->
                when (position) {
                    0 -> tab.text = getString(R.string.favorite_tracks)
                    1 -> tab.text = getString(R.string.playlists)
                }
            }
        tabLayoutMediator.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabLayoutMediator.detach()
    }
}