package com.the.app.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.the.app.*
import com.the.app.adapters.AdapterAlbums
import com.the.app.databinding.FragmentLoadBinding
import com.the.app.models.Album
import com.the.app.viewmodels.ViewModelLoad


class FragmentLoad : Fragment() {

    private lateinit var binding: FragmentLoadBinding
    private lateinit var adapter: AdapterAlbums
    private lateinit var viewModel: ViewModelLoad

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val v = inflater.inflate(R.layout.fragment_load, container, false)
        binding = FragmentLoadBinding.bind(v)
        viewModel = ViewModelProvider(this).get(ViewModelLoad::class.java)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        load()
        viewModel.albums.observe(viewLifecycleOwner, { showAlbums(it) })
        viewModel.status.observe(viewLifecycleOwner, { showRequestState(it) })
    }

    private fun initViews() {
        binding.swipeRefreshAlbums.setOnRefreshListener { refresh() }
        binding.recyclerAlbums.setHasFixedSize(true)
        adapter = AdapterAlbums.AlbumsLoadingAdapter(null) { open(it) }
        binding.recyclerAlbums.adapter = adapter
    }

    private fun open(album: Album?) {
        findNavController().navigate(
            R.id.nav_info,
            Bundle().apply {
                putParcelable(BUNDLE_KEY_ALBUM, album)
                putBoolean(BUNDLE_KEY_FROM_SAVED_FOLDER, false)
            }
        )
    }

    private fun showAlbums(albums: List<Album>?) {
        adapter.setAlbums(albums)
        if (albums == null || albums.isEmpty())
            Toast.makeText(requireContext(), R.string.noAlbums, Toast.LENGTH_LONG).show()
    }

    private fun showRequestState(status: TransactionsState) {
        when (status) {
            TransactionsState.LOADING_ERROR -> {
                binding.progress.hide()
                binding.swipeRefreshAlbums.isRefreshing = false
                binding.status.showStatus(R.string.error_loading)
            }
            TransactionsState.LOADING_SUCCEED -> {
                binding.progress.hide()
                binding.status.hide()
                binding.swipeRefreshAlbums.isRefreshing = false
            }
            TransactionsState.NO_NETWORK -> {
                binding.progress.hide()
                binding.swipeRefreshAlbums.isRefreshing = false
                binding.status.showStatus(R.string.noNetwork)
            }
            else -> return
        }
    }

    private fun load() {
        binding.progress.show()
        binding.status.showStatus(R.string.loading)
        viewModel.refreshOrload()
    }

    private fun refresh() {
        binding.status.showStatus(R.string.refreshing)
        viewModel.refreshOrload()
    }


}