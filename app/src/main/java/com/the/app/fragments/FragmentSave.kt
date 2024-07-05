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
import com.the.app.databinding.FragmentSaveBinding
import com.the.app.models.Album
import com.the.app.viewmodels.ViewModelSaved


class FragmentSave : Fragment() {
    private lateinit var binding: FragmentSaveBinding
    private lateinit var adapter: AdapterAlbums
    private lateinit var viewModel: ViewModelSaved
    private var deletedAlbumPosition: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val v = inflater.inflate(R.layout.fragment_save, container, false)
        binding = FragmentSaveBinding.bind(v)
        viewModel = ViewModelProvider(this).get(ViewModelSaved::class.java)
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
        binding.swipeRefreshAlbums.isEnabled = false
        binding.recAlbums.setHasFixedSize(true)
        adapter = AdapterAlbums.AlbumsSavedAdapter(
            null, { open(it) }) { pos, id ->
            deletedAlbumPosition = pos
            remove(id)
        }
        binding.recAlbums.adapter = adapter
    }

    private fun open(album: Album?) {
        findNavController().navigate(
            R.id.nav_info,
            Bundle().apply {
                putParcelable(BUNDLE_KEY_ALBUM, album)
                putBoolean(BUNDLE_KEY_FROM_SAVED_FOLDER, true)
            }
        )
    }

    private fun load() {
        binding.progress.show()
        binding.tvStatus.showStatus(R.string.loading)
        viewModel.loading()
    }

    private fun showAlbums(albums: List<Album>?) {
        if (albums != null && albums.isNotEmpty())
            binding.tvStatus.hide()
        else
            binding.tvStatus.showStatus(R.string.noSavedAlbums)
        adapter.setAlbums(albums)
    }

    private fun remove(albumId: Int) {
        binding.progress.show()
        binding.tvStatus.showStatus(R.string.removing)
        viewModel.removeing(albumId)
    }

    private fun showRequestState(status: TransactionsState) {

        when (status) {
            TransactionsState.LOADING_SUCCEED -> {
                binding.progress.hide()
            }
            TransactionsState.LOADING_ERROR -> {
                binding.progress.hide()
                binding.tvStatus.showStatus(R.string.error_loading)
            }
            TransactionsState.REMOVING_SUCCEED -> {
                deletedAlbumPosition?.let { it ->
                    binding.progress.hide()
                    binding.tvStatus.hide()
                    val wasTheLastAlbum = adapter.removeAlbum(it)
                    wasTheLastAlbum?.let { if (it) binding.tvStatus.showStatus(R.string.noSavedAlbums) }
                    Toast.makeText(requireContext(), R.string.successRemoved, Toast.LENGTH_SHORT)
                        .show()
                    deletedAlbumPosition = null
                }
            }
            TransactionsState.REMOVING_FAILED -> {
                binding.progress.hide()
                binding.tvStatus.hide()
                adapter.enableIconRemove()
                Toast.makeText(requireContext(), R.string.error_removing, Toast.LENGTH_LONG).show()
            }
            else -> return
        }
    }


}